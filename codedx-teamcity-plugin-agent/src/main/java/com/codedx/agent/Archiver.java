/*
 * Copyright (c) 2018. Code Dx, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codedx.agent;

import jetbrains.buildServer.util.pathMatcher.AntPatternFileCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {

	public static File archive(File workspace, String paths, String excludePaths, String prefix) throws IOException {
		final List<File> files = AntPatternFileCollector.scanDir(workspace, splitFileWildcards(paths), splitFileWildcards(excludePaths), null);

		Path workspaceDir = Paths.get(workspace.getCanonicalPath());
		Path tempFile = Files.createTempFile(prefix, ".zip");
		File zip = tempFile.toFile();

		try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zip))) {
			for (File file : files) {
				Path pathToFile = Paths.get(file.getCanonicalPath());
				String pathInZip = workspaceDir.relativize(pathToFile).toString();
				ZipEntry e = new ZipEntry(pathInZip);
				zout.putNextEntry(e);

				try (FileInputStream fis = new FileInputStream(file)) {
					addFileToZip(zout, fis);
				}
			}
			zout.close();
		}
		return zip;
	}

	public static void addFileToZip(ZipOutputStream zout, FileInputStream fis) throws IOException {
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zout.write(bytes, 0, length);
		}
		zout.closeEntry();
		fis.close();
	}

	private static String[] splitFileWildcards(final String string) {
		if (string != null) {
			final String[] split = string.split(",");
			for(int i = 0; i < split.length; i++) {
				split[i] = split[i].trim();
			}
			return split;
		}
		return new String[0];
	}
}
