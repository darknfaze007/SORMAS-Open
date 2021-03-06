package de.symeda.sormas.app.backend.sample;

import java.util.List;

import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public class SampleTestDtoHelper extends AdoDtoHelper<SampleTest, SampleTestDto> {

    @Override
    protected Class<SampleTest> getAdoClass() {
        return SampleTest.class;
    }

    @Override
    protected Class<SampleTestDto> getDtoClass() {
        return SampleTestDto.class;
    }

    @Override
    protected Call<List<SampleTestDto>> pullAllSince(long since) {
        return RetroProvider.getSampleTestFacade().pullAllSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<SampleTestDto> sampleTestDtos) {
        throw new UnsupportedOperationException("Can't change sample tests in app");
    }

    @Override
    protected void fillInnerFromDto(SampleTest target, SampleTestDto source) {

        target.setSample(DatabaseHelper.getSampleDao().getByReferenceDto(source.getSample()));
        target.setTestDateTime(source.getTestDateTime());
        target.setTestResult(source.getTestResult());
        target.setTestType(source.getTestType());
    }

    @Override
    protected void fillInnerFromAdo(SampleTestDto dto, SampleTest ado) {
        if(ado.getSample() != null) {
            Sample sample = DatabaseHelper.getSampleDao().queryForId(ado.getSample().getId());
            dto.setSample(SampleDtoHelper.toReferenceDto(sample));
        } else {
            dto.setSample(null);
        }

        dto.setTestDateTime(ado.getTestDateTime());
        dto.setTestResult(ado.getTestResult());
        dto.setTestType(ado.getTestType());
    }
}
