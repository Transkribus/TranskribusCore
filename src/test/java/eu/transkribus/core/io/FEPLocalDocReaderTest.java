package eu.transkribus.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.FEPLocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;

import org.eclipse.core.runtime.IProgressMonitor;

public class FEPLocalDocReaderTest {
	private static final Logger logger = LoggerFactory.getLogger(FEPLocalDocReaderTest.class);
	
	private static final String BASE = "/mnt/dea_scratch/TRP/";	
	public static final String FEP_TO_TRANSCRIBUS_DOC = BASE + "FEP_TO_TRANSCRIBUS_TEST/AV_1985";
	
	public static void testFEPToTranscribus() {
		try {
			IProgressMonitor m = new IProgressMonitor() {
				@Override public void worked(int work) {
					logger.info("worked: "+work);
				}
				
				@Override public void subTask(String name) {
				}
				
				@Override public void setTaskName(String name) {
				}
				
				@Override public void setCanceled(boolean value) {
				}
				
				@Override public boolean isCanceled() {
					return false;
				}
				
				@Override public void internalWorked(double work) {
				}
				
				@Override public void done() {
				}
				
				@Override public void beginTask(String name, int totalWork) {
				}
			};
			
			TrpDoc doc = FEPLocalDocReader.loadFEPDoc(FEP_TO_TRANSCRIBUS_DOC, true, true, true, true, m);
			
			logger.info("--- Finished tests in "+FEPLocalDocReaderTest.class.getSimpleName()+" ---");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}	

	public static void main(String[] args) {
		testFEPToTranscribus();
	}

}
