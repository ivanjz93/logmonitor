package storm;

import entity.Message;
import entity.Record;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.springframework.beans.BeanUtils;
import utils.MonitorHandler;

import java.util.Map;

public class PrepareRecordBolt extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        Message message = (Message)input.getValueByField("message");
        String appId = input.getStringByField("appId");
        MonitorHandler.notify(appId, message);
        Record record = new Record();
        BeanUtils.copyProperties(record, message);
        collector.emit(new Values(record));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("record"));
    }
}
