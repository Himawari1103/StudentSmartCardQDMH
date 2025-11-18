package com.qdmh.studentsmartcardqdmh.client.app.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    // Step 1: Create a private static instance of SessionFactory
    private static SessionFactory sessionFactory;

    // Step 2: Make the constructor private to prevent instantiation
    private HibernateUtil() {}

    // Step 3: Provide a public static method to get the SessionFactory instance
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    try {
                        // Load configuration and build SessionFactory
                        sessionFactory = new Configuration()
                                .configure()
                                .buildSessionFactory();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to create SessionFactory");
                    }
                }
            }
        }
        return sessionFactory;
    }

    // Optional: Provide a method to close the SessionFactory
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
