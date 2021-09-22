package hu.futureofmedia.mediortest.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactListItem {
    private long id;
    private String fullName;
    private String company;
    private String email;
    private String phoneNumber;
}
