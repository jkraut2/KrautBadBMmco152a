/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.touro.mco152.bm.persist;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * EM - Entity Manager Singleton providing access to an instance
 * of the Java Persistence EntityManager class. For use in persisting info about
 * new benchmark runs, and reading
 * information about previous benchmark runs.
 * 
 * @author James
 * 
 */
public class EM {   //TODO  Come up with a better name (besides EntityManager  :-)  
    
    private static EntityManager em = null;
    
    public static EntityManager getEntityManager() {
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jDiskMarkPU");
            em = emf.createEntityManager();
        }
        return em;
    }
}
