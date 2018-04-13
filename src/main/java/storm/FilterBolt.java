package storm;

import entity.Message;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import utils.MonitorHandler;

import java.util.Map;

public class FilterBolt extends BaseRichBolt {
    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String line = input.getString(0);
        Message message = MonitorHandler.parser(line);
        if(message == null) {
            return;
        }

        if(MonitorHandler.trigger(message)) {
            collector.emit(new Values(message.getAppId(), message));
        }

        MonitorHandler.scheduleLoad();

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("appId", "message"));
    }
}
