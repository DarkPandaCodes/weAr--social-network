//package com.community.weare.Configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
////@ComponentScan
//@EnableTransactionManagement
//public class PersistenceJPAConfig{
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean
//    entityManagerFactoryBean(){
//        //...???
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(){
//        JpaTransactionManager transactionManager
//                = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(
//                entityManagerFactoryBean().getObject() );
//        return transactionManager;
//    }
//}
