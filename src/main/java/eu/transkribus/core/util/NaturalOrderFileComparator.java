package eu.transkribus.core.util;

import java.io.File;
import java.util.Comparator;

public class NaturalOrderFileComparator implements Comparator<File> {
		private final NaturalOrderComparator comp;
	
		public NaturalOrderFileComparator() {
			comp = new NaturalOrderComparator();
		}

		@Override
		public int compare(File arg0, File arg1) {
			return comp.compare(arg0.getName(), arg1.getName());
		}
}
