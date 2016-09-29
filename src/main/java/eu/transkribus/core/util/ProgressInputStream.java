package eu.transkribus.core.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressInputStream extends FilterInputStream {
	private final static Logger logger = LoggerFactory.getLogger(ProgressInputStream.class);
	
    private final long maxNumBytes;
    private volatile long totalNumBytesRead;
    
    private final PropertyChangeSupport propertyChangeSupport;
    List<ProgressInputStreamListener> listener = new LinkedList<ProgressInputStreamListener>();
    
    private boolean closed = false;
    
    public static abstract class ProgressInputStreamListener {
    	ProgressInputStream pi=null;
    	
    	public void setProgressInputStream(ProgressInputStream pi) {
    		this.pi = pi;
    	}
    	
    	public void abort() {
    		if (pi != null) {
    			logger.info("aborting ProgressInputStream...");
    			try {
					pi.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
    		}
    	}
    	
    	public abstract void progress(long oldBytesRead, long bytesRead, long totalBytes);
    }

    public ProgressInputStream(InputStream in, long maxNumBytes) {
        super(in);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.maxNumBytes = maxNumBytes;
    }

    public long getMaxNumBytes() {
        return maxNumBytes;
    }

    public long getTotalNumBytesRead() {
        return totalNumBytesRead;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    public void addProgressInputStreamListener(ProgressInputStreamListener l) {
    	if (l!=null) {
    		l.setProgressInputStream(this);
    		listener.add(l);
    	}
    }
    
    public void removeProgressInputStreamListener(ProgressInputStreamListener l) {
    	if (l!=null) {
    		l.setProgressInputStream(null);
    		listener.remove(l);
    	}
    }

    @Override
    public int read() throws IOException {
    	logger.trace("reading one byte");
        int b = super.read();
        updateProgress(1);
        return b;
    }

//    @Override
//    public int read(byte[] b) throws IOException {
//    	logger.debug("read2");
//        return (int)updateProgress(super.read(b));
//    }
    
    @Override public void close() throws IOException {
    	this.closed = true;
        super.close();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
//    	if (closed)
//    		return 0;
    	
    	logger.trace("reading into buffer, off="+off+" / len="+len);
        return (int)updateProgress(super.read(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        return updateProgress(super.skip(n));
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private long updateProgress(long numBytesRead) {
        if (numBytesRead > 0) {
            long oldTotalNumBytesRead = this.totalNumBytesRead;
            this.totalNumBytesRead += numBytesRead;
            
            propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead, this.totalNumBytesRead);
            for (ProgressInputStreamListener l : listener) {
            	l.progress(oldTotalNumBytesRead, totalNumBytesRead, maxNumBytes);
            }
            
//            logger.debug("read: "+totalNumBytesRead+"/"+maxNumBytes);
        }

        return numBytesRead;
    }
}