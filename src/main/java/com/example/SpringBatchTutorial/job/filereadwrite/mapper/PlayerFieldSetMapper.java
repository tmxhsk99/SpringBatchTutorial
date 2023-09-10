package com.example.SpringBatchTutorial.job.filereadwrite.mapper;

import com.example.SpringBatchTutorial.job.filereadwrite.dto.Player;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PlayerFieldSetMapper implements FieldSetMapper<Player> {


    @Override
    public Player mapFieldSet(FieldSet fieldSet) throws BindException {
        return Player.builder()
                .ID(fieldSet.readString(0))
                .lastName(fieldSet.readString(1))
                .firstName(fieldSet.readString(2))
                .position(fieldSet.readString(3))
                .birthYear(fieldSet.readInt(4))
                .debutYear(fieldSet.readInt(5))
                .build();
    }
}
