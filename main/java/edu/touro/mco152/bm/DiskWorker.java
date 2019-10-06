
package edu.touro.mco152.bm;



import static edu.touro.mco152.bm.App.KILOBYTE;
import static edu.touro.mco152.bm.App.MEGABYTE;
import static edu.touro.mco152.bm.App.blockSizeKb;
import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.App.numOfBlocks;
import static edu.touro.mco152.bm.App.numOfMarks;
import static edu.touro.mco152.bm.App.testFile;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;

/**
 * Thread running the disk benchmarking. only one of these threads can run at
 * once.
 */
public class DiskWorker extends SwingWorker <Boolean, DiskMark> {
    
    @Override
    protected Boolean doInBackground() throws Exception {
        
        System.out.println("*** starting new worker thread");
        msg("Running readTest "+App.readTest+"   writeTest "+App.writeTest);
        msg("num files: "+App.numOfMarks+", num blks: "+App.numOfBlocks
           +", blk size (kb): "+App.blockSizeKb+", blockSequence: "+App.blockSequence);
        
        int wUnitsComplete = 0,
            rUnitsComplete = 0,
            unitsComplete;
        
        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;
        
        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }
   
        DiskMark wMark, rMark;
        checkResetData();
        Gui.updateLegend();


        
        int startFileNum = App.nextMarkNumber;
        
        if(App.writeTest) {
                writeTest();
            }

        }
        
        
        if (App.readTest) {
            readTest(); 
        }
        App.nextMarkNumber += App.numOfMarks;      
        return true;
    }
    
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((m) -> {
            if (m.type==DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(m);
            } else {
                Gui.addReadMark(m);
            }
        });
    }
    
    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    public void writeTest() {
        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        if (App.multiFile == false) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }
        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !isCancelled(); m++) {

            if (App.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);
            measureData(wMark,m);
            App.updateMetrics(wMark);
            publish(wMark);

            setRunVars();}
            persistData();
            Gui.runPanel.addRun(run);


    }

    public void readTest(){
        DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
        setRunParams();
        
        

        msg("disk info: ("+ run.getDiskInfo()+")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        for (int m=startFileNum; m<startFileNum+App.numOfMarks && !isCancelled(); m++) {

            if (App.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator+"testdata"+m+".jdm");
            }
            rMark = new DiskMark(READ);
            measureData(rMark,m);
            
            App.updateMetrics(rMark);
            publish(rMark);
            setRunVars();


        }

        persistData();
        Gui.runPanel.addRun(run);
    }


    public void checkResetData(){
        if (App.autoReset == true) {
            App.resetTestData();
            Gui.resetTestData();
        }
    }

        public void persistData() {
            EntityManager em = EM.getEntityManager();
            em.getTransaction().begin();
            em.persist(run);
            em.getTransaction().commit();
        }

        public void setRunParams(){
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
        }

        public void setRunVars(){
            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }
        measureData(DiskMark d, int m){
            if(d.type == DiskMark.MarkType.WRITE){
                d.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek(rLoc * blockSize);
                        } else {
                            rAccFile.seek(b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                        setProgress((int) percentComplete);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + d.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            }
            else if(d.type == DiskMark.MarkType.READ)
            {
                rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,"r")) {
                    for (int b=0; b<numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks-1);
                            rAccFile.seek(rLoc*blockSize);
                        } else {
                            rAccFile.seek(b*blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float)unitsComplete/(float)unitsTotal * 100f;
                        setProgress((int)percentComplete);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double)elapsedTimeNs / (double)1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:"+m+" READ IO is "+rMark.getBwMbSec()+" MB/s    "
                    + "(MBread "+mbRead+" in "+sec+" sec)");
            }
            
        }
    }   