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

import com.codedx.common.CodeDxConstants;
import com.codedx.client.model.GroupedCount;
import java.util.List;

public class CodeDxBuildStatistics {
	private List<GroupedCount> groupedSeverityCounts;
	private List<GroupedCount> groupedStatusCounts;

	public enum Group {
		SEVERITY,
		STATUS
	}

	public CodeDxBuildStatistics(List<GroupedCount> groupedSeverityCounts, List<GroupedCount> groupedStatusCounts) {
		this.groupedSeverityCounts = groupedSeverityCounts;
		this.groupedStatusCounts = groupedStatusCounts;
	}

	public List<GroupedCount> getGroupedSeverityCounts() {
		return groupedSeverityCounts;
	}

	public List<GroupedCount> getGroupedStatusCounts() {
		return groupedStatusCounts;
	}

	public static int getNumberOfFindingsForGroupAndName(final List<GroupedCount> groupedCounts, String name) {
		if(name.equals(CodeDxConstants.TOTAL)) {
			return getTotalFindingsForGroup(groupedCounts);
		}

		int count = 0;
		count = groupedCounts.stream()
			.filter(gc -> gc.getName().contains(name))
			.mapToInt(gc -> gc.getCount())
			.sum();

		return count;
	}

	public static int getTotalFindingsForGroup(List<GroupedCount> groupedCounts) {
		return groupedCounts.stream().mapToInt(gc -> gc.getCount()).sum();
	}

}
