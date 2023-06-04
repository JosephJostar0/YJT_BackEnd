package edu.web.yjt_backend.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddPaperRequest implements Serializable {
    private static final long serialVersionUID = 6592864678216221106L;
    private String name;
    private String description;
    private List<Long> questions;
    private long userId;
    private byte isPublic;
}
