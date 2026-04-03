package it.deltascientia.mapper;

import it.deltascientia.dto.TrialValueRequest;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.Trial;
import it.deltascientia.model.TrialValue;
import it.deltascientia.model.Variable;
import it.deltascientia.model.VariableType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

class TrialValueMapperTest {

    @Test
    void toEntity_shouldConvertRequestToTrialValue() {
        Experiment experiment = Experiment.builder().id(1L).build();
        Trial trial = Trial.builder()
                .id(10L)
                .trialNumber(1)
                .values(new ArrayList<>())
                .experiment(experiment)
                .build();
        VariableType vt = VariableType.builder().id(2L).name("pH").build();
        Variable variable = Variable.builder().id(5L).variableType(vt).build();
        var request = TrialValueRequest.builder()
                .variableId(5L)
                .valueText(null)
                .valueNumeric(7.4)
                .valueLongText(null)
                .build();

        TrialValue result = TrialValueMapper.toEntity(request, trial, variable);

        assertThat(result.getTrial()).isEqualTo(trial);
        assertThat(result.getVariable()).isEqualTo(variable);
        assertThat(result.getValueNumeric()).isEqualTo(7.4);
        assertThat(result.getValueText()).isNull();
        assertThat(result.getValueLongText()).isNull();
    }
}
