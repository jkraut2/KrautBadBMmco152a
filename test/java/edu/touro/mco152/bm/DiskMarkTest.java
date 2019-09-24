package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

class DiskMarkTest {

    DiskMark diskmark = new DiskMark(DiskMark.MarkType.READ);

    @org.junit.jupiter.api.Test
    void getCumMin() {
        assertEquals(0, diskmark.getCumMin());
    }

    /**
     * This tests for conformance (c in correct) making sure that the number returns formatted.
     */
    @Test
    void getMinAsString(){
        diskmark.setCumMin(1.5);
        assertEquals("1.5" ,diskmark.getMinAsString());
    }

    /**
     * To test if a number is in the cum-range. This is a test of range (part of correct).
     */
    @org.junit.jupiter.api.Test
    void testCumRange() {
        diskmark.setCumMax(5);
        diskmark.setCumMin(1);
        int myNum = 4;
        assertTrue(diskmark.getCumMin() <= myNum && myNum <= diskmark.getCumMax());
    }

    /**
     * This test is the B in bicep because we can use it to test boundary conditions.
     * We can test to see if the values are actually going in. This test aso fulfils the e in correct as
     *  it test if the value exists. (This is a paramateried test as well).
     */
    @ParameterizedTest
    @ValueSource(doubles = { 5.6, 8.3, 12.2 })
    void setCumMin(double candidate){
        diskmark.setCumMin(candidate);
        assertNotNull(candidate);

    }

}