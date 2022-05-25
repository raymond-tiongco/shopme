package com.shopme.admin.entity;

public enum Roles {
    Admin(1,"Manage everything"),
    Salesperson(2,"Manage product price, customers, shipping, orders and sales report"),
    Editor(3,"Manage categories, brands, products, articles and menus"),
    Shipper(4,"View products, view orders and update order status"),
    Assistant(5,"Manage product price, customers, shipping, orders and sales report");

    public final int ROLE_NUMBER;
    public final String DESCRIPTION;

    Roles(int roleNumber, String description) {
        this.ROLE_NUMBER = roleNumber;
        this.DESCRIPTION = description;
    }
}
