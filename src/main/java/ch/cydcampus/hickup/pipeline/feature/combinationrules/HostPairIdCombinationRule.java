package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

public class HostPairIdCombinationRule implements FeatureCombinationRule {
    
    int outputIndex;

    @Override
    public HostPairIdCombinationRule setIndices(int[] indices) {
        // ignore as feature indices are statically known for level 0   
        return this;
    }

    @Override
    public HostPairIdCombinationRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void combine(Abstraction abstraction) {
        String hostPairId = null;
        Feature srcFeature = abstraction.getFeatures()[PipelineConfig.SRC_IP_INDEX];
        Feature dstFeature = abstraction.getFeatures()[PipelineConfig.DST_IP_INDEX];
        String srcIp = srcFeature.toString();
        String dstIp = dstFeature.toString();
        if(srcIp.compareTo(dstIp) < 0) {
            hostPairId = srcIp + "-" + dstIp;
        } else {
            hostPairId = dstIp + "-" + srcIp;
        }
        abstraction.getFeatures()[outputIndex].set(hostPairId);
    }

}
