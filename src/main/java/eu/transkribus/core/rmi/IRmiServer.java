package eu.transkribus.core.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.bind.JAXBException;

import eu.transkribus.core.rmi.util.NcsrToolException;

public interface IRmiServer extends Remote {

	public String checkConnection() throws RemoteException;

	public String getBlockSeg(final String imgKey, String pcGts, boolean usePrintspaceOnly)
			throws IOException, NcsrToolException, JAXBException;

	//	public PcGtsType getBlockSeg(File origImgFile, File binImgFile, PcGtsType pc,
	//			boolean usePrintspaceOnly) throws IOException, NcsrToolException, JAXBException;
	public String getLineSeg(final String imgKey, String pcGts, final List<String> regIds)
			throws IOException, NcsrToolException, JAXBException;

	//	public PcGtsType getLineSeg(File binImgFile, PcGtsType pc, final List<String> regIds) throws IOException, NcsrToolException, JAXBException;
	//	public File binarize(File imgFile) throws IOException, NcsrToolException;
	public String getWordSeg(String imgKey, String pcGts, List<String> regIds) throws IOException,
			NcsrToolException, JAXBException;

	public String addBaselines(String imgKey, String pcGts, final List<String> regIds)
			throws IOException, NcsrToolException, JAXBException;
}