package com.rubiconproject.oss.kv.distributed;

import java.io.IOException;

public interface Configurator {

	public Configuration getConfiguration() throws IOException;
}
