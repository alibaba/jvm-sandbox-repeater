package org.tony.console.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tony.console.biz.DataBizService;
import org.tony.console.biz.model.chart.ContentDataRecord;
import org.tony.console.biz.model.chart.PanelData;
import org.tony.console.common.Result;
import org.tony.console.service.AppStaticService;
import org.tony.console.service.model.AppStaticDataDO;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author peng.hu1
 * @Date 2023/2/26 10:25
 */
@RestController
@RequestMapping("/api/v1/static")
public class StaticApi {

    @Resource
    AppStaticService appStaticService;

    @Resource
    DataBizService dataBizService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Result<AppStaticDataDO> query(@RequestParam String appName) {
        AppStaticDataDO appStaticDataDO = appStaticService.queryStaticDataOfNow(appName);
        return Result.buildSuccess(appStaticDataDO, "成功");
    }

    @RequestMapping(value = "/queryRecordChartData", method = RequestMethod.GET)
    public Result<List<ContentDataRecord>> queryRecordChartData(@RequestParam String appName) {
        return Result.buildSuccess(
                dataBizService.queryRecordChart(appName),
                "成功"
        );
    }

    @RequestMapping(value = "/queryBuRecordChart", method = RequestMethod.GET)
    public Result<List<ContentDataRecord>> queryBuRecordChart(@RequestParam String appName) {
        return Result.buildSuccess(
                dataBizService.queryBuRecordChart(appName),
                "成功"
        );
    }


    @RequestMapping(value = "/queryPanelData", method = RequestMethod.GET)
    public Result<PanelData> queryPanelData(@RequestParam String appName) {
        return Result.buildSuccess(
                dataBizService.queryPanelData(appName),
                "成功"
        );
    }
}
