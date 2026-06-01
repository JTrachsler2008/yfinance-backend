package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * A single earnings report row.
 */

@Schema(name = "EarningRow", description = "A single earnings report row.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class EarningRow {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate earningsDate = null;

  private @Nullable BigDecimal reportedEps = null;

  private @Nullable BigDecimal estimatedEps = null;

  private @Nullable BigDecimal revenue = null;

  private @Nullable BigDecimal surprise = null;

  private @Nullable BigDecimal surprisePercent = null;

  public EarningRow earningsDate(@Nullable LocalDate earningsDate) {
    this.earningsDate = earningsDate;
    return this;
  }

  /**
   * Get earningsDate
   * @return earningsDate
   */
  @Valid 
  @Schema(name = "earnings_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("earnings_date")
  public @Nullable LocalDate getEarningsDate() {
    return earningsDate;
  }

  public void setEarningsDate(@Nullable LocalDate earningsDate) {
    this.earningsDate = earningsDate;
  }

  public EarningRow reportedEps(@Nullable BigDecimal reportedEps) {
    this.reportedEps = reportedEps;
    return this;
  }

  /**
   * Get reportedEps
   * @return reportedEps
   */
  @Valid 
  @Schema(name = "reported_eps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reported_eps")
  public @Nullable BigDecimal getReportedEps() {
    return reportedEps;
  }

  public void setReportedEps(@Nullable BigDecimal reportedEps) {
    this.reportedEps = reportedEps;
  }

  public EarningRow estimatedEps(@Nullable BigDecimal estimatedEps) {
    this.estimatedEps = estimatedEps;
    return this;
  }

  /**
   * Get estimatedEps
   * @return estimatedEps
   */
  @Valid 
  @Schema(name = "estimated_eps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("estimated_eps")
  public @Nullable BigDecimal getEstimatedEps() {
    return estimatedEps;
  }

  public void setEstimatedEps(@Nullable BigDecimal estimatedEps) {
    this.estimatedEps = estimatedEps;
  }

  public EarningRow revenue(@Nullable BigDecimal revenue) {
    this.revenue = revenue;
    return this;
  }

  /**
   * Get revenue
   * @return revenue
   */
  @Valid 
  @Schema(name = "revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revenue")
  public @Nullable BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(@Nullable BigDecimal revenue) {
    this.revenue = revenue;
  }

  public EarningRow surprise(@Nullable BigDecimal surprise) {
    this.surprise = surprise;
    return this;
  }

  /**
   * Get surprise
   * @return surprise
   */
  @Valid 
  @Schema(name = "surprise", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("surprise")
  public @Nullable BigDecimal getSurprise() {
    return surprise;
  }

  public void setSurprise(@Nullable BigDecimal surprise) {
    this.surprise = surprise;
  }

  public EarningRow surprisePercent(@Nullable BigDecimal surprisePercent) {
    this.surprisePercent = surprisePercent;
    return this;
  }

  /**
   * Get surprisePercent
   * @return surprisePercent
   */
  @Valid 
  @Schema(name = "surprise_percent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("surprise_percent")
  public @Nullable BigDecimal getSurprisePercent() {
    return surprisePercent;
  }

  public void setSurprisePercent(@Nullable BigDecimal surprisePercent) {
    this.surprisePercent = surprisePercent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EarningRow earningRow = (EarningRow) o;
    return Objects.equals(this.earningsDate, earningRow.earningsDate) &&
        Objects.equals(this.reportedEps, earningRow.reportedEps) &&
        Objects.equals(this.estimatedEps, earningRow.estimatedEps) &&
        Objects.equals(this.revenue, earningRow.revenue) &&
        Objects.equals(this.surprise, earningRow.surprise) &&
        Objects.equals(this.surprisePercent, earningRow.surprisePercent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(earningsDate, reportedEps, estimatedEps, revenue, surprise, surprisePercent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EarningRow {\n");
    sb.append("    earningsDate: ").append(toIndentedString(earningsDate)).append("\n");
    sb.append("    reportedEps: ").append(toIndentedString(reportedEps)).append("\n");
    sb.append("    estimatedEps: ").append(toIndentedString(estimatedEps)).append("\n");
    sb.append("    revenue: ").append(toIndentedString(revenue)).append("\n");
    sb.append("    surprise: ").append(toIndentedString(surprise)).append("\n");
    sb.append("    surprisePercent: ").append(toIndentedString(surprisePercent)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

