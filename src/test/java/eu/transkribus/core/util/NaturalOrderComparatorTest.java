package eu.transkribus.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;

import eu.transkribus.core.util.NaturalOrderComparator;

public class NaturalOrderComparatorTest {
	public static void main(String[] args) {
		//	File dir = new File("/mnt/dea_scratch/TRP/test/II._ZvS_1908_1.Q");
		File dir = new File("/mnt/dea_scratch/TRP/test/Schauplatz_test");

		File[] imgs = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		});

		Comparator<String> naturalOrderComp = new NaturalOrderComparator();

		Map<String, String> map = new TreeMap<>(naturalOrderComp);

		for (File i : imgs) {
			String name = FilenameUtils.getBaseName(i.getName());
			String nameComp = i.getName();
			map.put(name, nameComp);
		}

		for (Entry<String, String> e : map.entrySet()) {
			System.out.println(e.getKey() + " - " + e.getValue());
		}

	}
}
