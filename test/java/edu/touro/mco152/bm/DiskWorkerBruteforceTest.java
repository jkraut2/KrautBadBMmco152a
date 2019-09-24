package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A series of tests for the DiskWorker class, which does the actual benchmarks.
 * Since DiskWorker extends SwingWorker and is non-SRP, a brute-force is taken
 * for to provide basic sanity tests.  Challenges include:
 *  - Members of several static classes must be set.
 *  - Details of results non directly available - but can re-read from database
 *  - Code runs in a separate thread courtesy of Swing, although its not set up completely.
 *
 * @author mcohen
 */
public class DiskWorkerBruteforceTest {

    /**
     * Run a basic sanity test using any settings (from pervious saved run) and make sure
     * the DiskWorket thread is able to run to completion and give back a return code
     * TODO Consider whether calling any other swing status methods in interval between execute() and get would be a good test.
     */
    @ParameterizedTest
    void sanityTestPrevSettings()
    {
        Boolean  tResult = null;    // return value to be placed here

        // Arrange test based on settings of properties (indeterminate) from last run
        setupDefaultAsPerProperties();
        DiskWorker worker = new DiskWorker();

        // Act by invoking the Swing execute which will call DiskWorker on separate thread
        worker.execute();

        // above line starts a separate thread, we have to wait for its completion
        try {
            tResult = worker.get();     // DiskWorker completed, gave a return code
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("get() while wait for DiskWorker was interrupted", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("get() while wait for DiskWorker failed", e);
        }

         /**
         * Right-BICEP - RIGHT.
         */
        // Assert: If DiskWorker fully executed, it should return True
        assertTrue(tResult);
    }


    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     */
    private void setupDefaultAsPerProperties()
    {

        /// Do the mimimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }
}
