package com.ixenit.liferay.angular.portlet.test.mock;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MockedResourceResponse extends BaseResourceResponse {

	@Override
	public PrintWriter getWriter() throws IOException {
		return new MockedPrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
	}

	private class MockedPrintWriter extends PrintWriter {

		public MockedPrintWriter(OutputStreamWriter outputStreamWriter) {
			super(outputStreamWriter);
		}

		@Override
		public void print(String string) {
			super.print(string);
		}
	}

}
