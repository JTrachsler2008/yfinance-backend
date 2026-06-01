package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * StockSplit
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class StockSplit {

  private String date;

  private BigDecimal ratio;

  public StockSplit() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public StockSplit(String date, BigDecimal ratio) {
    this.date = date;
    this.ratio = ratio;
  }

  public StockSplit date(String date) {
    this.date = date;
    return this;
  }

  /**
   * The date of the stock split
   * @return date
   */
  @NotNull 
  @Schema(name = "date", description = "The date of the stock split", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public StockSplit ratio(BigDecimal ratio) {
    this.ratio = ratio;
    return this;
  }

  /**
   * The split ratio (e.g., 2.0 for a 2-for-1 split)
   * @return ratio
   */
  @NotNull @Valid 
  @Schema(name = "ratio", description = "The split ratio (e.g., 2.0 for a 2-for-1 split)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ratio")
  public BigDecimal getRatio() {
    return ratio;
  }

  public void setRatio(BigDecimal ratio) {
    this.ratio = ratio;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StockSplit stockSplit = (StockSplit) o;
    return Objects.equals(this.date, stockSplit.date) &&
        Objects.equals(this.ratio, stockSplit.ratio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, ratio);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StockSplit {\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    ratio: ").append(toIndentedString(ratio)).append("\n");
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

