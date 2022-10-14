package com.thoughtworks.codepairing.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingCart {
    private List<Product> products;
    private Customer customer;

    public ShoppingCart(Customer customer, List<Product> products) {
        this.customer = customer;
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public Order checkout() {
        double totalPrice = 0;
        Map<String, Integer> bulkProductCodeCountMap = new HashMap<>();
        Map<String, Double> bulkProductCodePriceMap = new HashMap<>();

        int loyaltyPointsEarned = 0;
        for (Product product : products) {
            double discount = 0;
            if (product.getProductCode().startsWith("DIS")) {
                int discountRate=Integer.parseInt(product.getProductCode().split("_")[1]);
                discount = product.getPrice() * discountRate/100;
                loyaltyPointsEarned += product.getPrice() / discountRate;
            } else if (product.getProductCode().startsWith("BULK_2")) {
                String productCode = product.getProductCode();

                bulkProductCodePriceMap.put(productCode, product.getPrice());
                if (bulkProductCodeCountMap.containsKey(productCode)) {
                    bulkProductCodeCountMap.put(productCode, bulkProductCodeCountMap.get(productCode) + 1);
                } else {
                    bulkProductCodeCountMap.put(productCode, 1);
                }
            } else {
                loyaltyPointsEarned += (product.getPrice() / 5);
            }

            totalPrice += product.getPrice() - discount;
        }
        for (String productCode : bulkProductCodeCountMap.keySet()) {
            int count = bulkProductCodeCountMap.get(productCode);
            totalPrice -= count / 3 * bulkProductCodePriceMap.get(productCode);
        }

        if (totalPrice > 500) {
            double extraPrice = totalPrice - 500;
            totalPrice -= extraPrice * 0.05;
        }
        return new Order(totalPrice, loyaltyPointsEarned);
    }

    @Override
    public String toString() {
        return "Customer: " + customer.getName() + "\n" + "Bought:  \n" + products.stream().map(p -> "- " + p.getName() + ", " + p.getPrice()).collect(Collectors.joining("\n"));
    }
}
