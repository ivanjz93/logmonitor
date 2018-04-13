package storm;

import entity.Record;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import utils.MonitorHandler;

import java.util.Map;

public class SaveBolt extends BaseRichBolt {
    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        Record record = (Record)input.getValueByField("record");
        MonitorHandler.save(record);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
