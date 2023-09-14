package org.tony.console.web.api;

import org.springframework.web.bind.annotation.*;
import org.tony.console.common.Result;
import org.tony.console.common.domain.PageResult;
import org.tony.console.common.domain.RecordBO;
import org.tony.console.common.domain.RecordDetailBO;
import org.tony.console.db.query.RecordQuery;
import org.tony.console.service.RecordService;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/record")
public class RecordApi {

    @Resource
    RecordService recordService;

    @RequestMapping(path = "list", method = RequestMethod.POST)
    public PageResult<RecordBO> queryRecord(
            @RequestBody RecordQuery recordQuery
    ) {
        recordQuery.setOrderByGmtCreateDesc(true);
        return recordService.queryMongo(recordQuery);
    }

    @RequestMapping(path = "detail", method = RequestMethod.POST)
    public Result<RecordDetailBO> queryDetail(@RequestBody RecordQuery params) {
        return recordService.getDetail(params);
    }

    @RequestMapping(path = "date", method = RequestMethod.GET)
    public Result<Date> queryDate() {
        return Result.builder().data(new Date()).build();
    }
}
