package ch.cydcampus.hickup.pipeline.feature;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class FlowIdCombinationRule implements FeatureCombinationRule {

    int outputIndex;

    @Override
    public FlowIdCombinationRule setIndices(int[] indices) {
        // ignore indices as we operate on level 0 and know them statically
        return this;
    }

    @Override
    public FlowIdCombinationRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void combine(Abstraction abstraction) {

        String flowId = null;
        Feature srcFeature = abstraction.getFeatures()[PipelineConfig.SRC_IP_INDEX];
        Feature dstFeature = abstraction.getFeatures()[PipelineConfig.DST_IP_INDEX];
        Feature srcPortFeature = abstraction.getFeatures()[PipelineConfig.SRC_PORT_INDEX];
        Feature dstPortFeature = abstraction.getFeatures()[PipelineConfig.DST_PORT_INDEX];
        Feature protocolFeature = abstraction.getFeatures()[PipelineConfig.PROTOCOL_INDEX];

        String srcIp = srcFeature.toString();
        String dstIp = dstFeature.toString();
        if(srcIp.compareTo(dstIp) < 0) {
            flowId = srcIp + ":" + srcPortFeature.toString() + "-" + dstIp + ":" + dstPortFeature.toString() + "-" + protocolFeature.toString();
        } else {
            flowId = dstIp + ":" + dstPortFeature.toString() + "-" + srcIp + ":" + srcPortFeature.toString() + "-" + protocolFeature.toString();
        }

        abstraction.getFeatures()[outputIndex].set(flowId);

    }

    

    
}
