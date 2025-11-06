package org.example;


import Product.Catalog;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Catalog> catalogs = new ArrayList<>(){{
           add(new Catalog("Смартфоны"));
           add(new Catalog("Комплектующие для ПК"));
           add(new Catalog("Бытовая техника"));
           add(new Catalog("Офис и мебель"));
        }};

        for (Catalog catalog : catalogs){
            System.out.println("\nКаталог - " + catalog.getCatalogName());
            catalog.displayProducts();
        }
    }
}