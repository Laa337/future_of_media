package hu.futureofmedia.mediortest.services.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedRequest {
    private int pageNumber;
    private String sortField;
    private String filter;
}
