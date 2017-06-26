package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;

public class GsonUtilTest {

	@Test 
	public void testSerializingUnserializingDocumentSelectionDescriptors() {
		List<DocumentSelectionDescriptor> dsds = new ArrayList<>();
		DocumentSelectionDescriptor d1 = new DocumentSelectionDescriptor(10);
		DocumentSelectionDescriptor d2 = new DocumentSelectionDescriptor(3);
		d1.addPage(new PageDescriptor(32));
		PageDescriptor pd = new PageDescriptor(20);
		pd.setRegionIds(new HashSet<String>(Arrays.asList("r1", "r2")));
		d2.addPage(pd);
		DocumentSelectionDescriptor d3 = new DocumentSelectionDescriptor(5);
		d3.addPage(10);
		
		dsds.add(d1);
		dsds.add(d2);
		dsds.add(d3);
		
		System.out.println("dsds orig = "+CoreUtils.toListString(dsds));
		for (DocumentSelectionDescriptor dd : dsds) {
			System.out.println(dd);
		}
		
		String jsonStr = GsonUtil.toJson(dsds);
		System.out.println("json = "+jsonStr);
		
//		List<DocumentSelectionDescriptor> dsdsUnmarshalled = GsonUtil.toList(jsonStr, DocumentSelectionDescriptor.class);
		List<DocumentSelectionDescriptor> dsdsUnmarshalled = GsonUtil.toDocumentSelectionDescriptorList(jsonStr);
		System.out.println("dsds unmarshalled = "+dsdsUnmarshalled);
		
		for (int i=0; i<3; ++i) {
			DocumentSelectionDescriptor dd = dsdsUnmarshalled.get(i);
			DocumentSelectionDescriptor ddOrig = dsds.get(i);
			
			Assert.assertEquals("Unserialized descriptor not equal to original one: "+dd+" / "+ddOrig, dd, ddOrig);
		}
		
	}
	
	public static void main(String[] args) {
		GsonUtilTest test = new GsonUtilTest();
		
		test.testSerializingUnserializingDocumentSelectionDescriptors();
		
	}

}
