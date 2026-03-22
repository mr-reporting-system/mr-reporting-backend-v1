import lombok.Data;

@Data
public class ApprovalCountsDTO {
    private long areaAdditions;
    private long areaDeletions;
    private long doctorAdditions;
    private long doctorDeletions;
    private long providerAdditions;
    private long providerDeletions;
}