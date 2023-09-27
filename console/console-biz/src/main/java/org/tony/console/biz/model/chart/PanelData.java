package org.tony.console.biz.model.chart;

import lombok.Data;

/**
 * @author peng.hu1
 * @Date 2023/2/26 11:35
 */
@Data
public class PanelData {

    private long totalRecord;

    private long totalRepeat;

    private long totalRecordYest;

    private double plus;
}
