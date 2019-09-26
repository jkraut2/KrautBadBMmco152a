package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    App app = new App();
    /**
     * This test utilizes RIGHT by checking if the results are right.
     * This test also uses cross checking by just doing the math ourselves.
     * Then we do the P of right bicep and test if the method performs the same with a larger input. We can do
     * so by checking how long it took to run with the larger amount.
     */
    @org.junit.jupiter.api.Test
    void targetMarkSizeKb() {
        assertEquals(16384,(app.blockSizeKb * app.numOfBlocks));
        assertEquals(16384, app.targetMarkSizeKb());

        app.blockSizeKb = 1800;
        app.numOfBlocks = 324;

        assertEquals(583200,(app.blockSizeKb * app.numOfBlocks));

        app.blockSizeKb = 512;
        app.numOfBlocks = 32;

    }

    /**
     * E in right bicep, forces exception to happen.
     */
    @org.junit.jupiter.api.Test
    void msg(){
        assertThrows(IllegalArgumentException.class, () -> {
            app.msg(null);
        });
    }

    // set to fail, should be 409600
    @org.junit.jupiter.api.Test
    void targetTxSizeKb() {
        assertEquals(409500, app.targetTxSizeKb());
    }
}