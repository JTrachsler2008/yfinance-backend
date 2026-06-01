package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Earnings history response for a symbol.
 */

@Schema(name = "EarningsResponse", description = "Earnings history response for a symbol.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class EarningsResponse {

  private String symbol;

  private String frequency;

  @Valid
  private List<@Valid EarningRow> rows = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate nextEarningsDate = null;

  private @Nullable BigDecimal lastEps = null;

  public EarningsResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EarningsResponse(String symbol, String frequency, List<@Valid EarningRow> rows) {
    this.symbol = symbol;
    this.frequency = frequency;
    this.rows = rows;
  }

  public EarningsResponse symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Get symbol
   * @return symbol
   */
  @NotNull 
  @Schema(name = "symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("symbol")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public EarningsResponse frequency(String frequency) {
    this.frequency = frequency;
    return this;
  }

  /**
   * Get frequency
   * @return frequency
   */
  @NotNull 
  @Schema(name = "frequency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("frequency")
  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public EarningsResponse rows(List<@Valid EarningRow> rows) {
    this.rows = rows;
    return this;
  }

  public EarningsResponse addRowsItem(EarningRow rowsItem) {
    if (this.rows == null) {
      this.rows = new ArrayList<>();
    }
    this.rows.add(rowsItem);
    return this;
  }

  /**
   * Get rows
   * @return rows
   */
  @NotNull @Valid 
  @Schema(name = "rows", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rows")
  public List<@Valid EarningRow> getRows() {
    return rows;
  }

  public void setRows(List<@Valid EarningRow> rows) {
    this.rows = rows;
  }

  public EarningsResponse nextEarningsDate(@Nullable LocalDate nextEarningsDate) {
    this.nextEarningsDate = nextEarningsDate;
    return this;
  }

  /**
   * Get nextEarningsDate
   * @return nextEarningsDate
   */
  @Valid 
  @Schema(name = "next_earnings_date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("next_earnings_date")
  public @Nullable LocalDate getNextEarningsDate() {
    return nextEarningsDate;
  }

  public void setNextEarningsDate(@Nullable LocalDate nextEarningsDate) {
    this.nextEarningsDate = nextEarningsDate;
  }

  public EarningsResponse lastEps(@Nullable BigDecimal lastEps) {
    this.lastEps = lastEps;
    return this;
  }

  /**
   * Get lastEps
   * @return lastEps
   */
  @Valid 
  @Schema(name = "last_eps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("last_eps")
  public @Nullable BigDecimal getLastEps() {
    return lastEps;
  }

  public void setLastEps(@Nullable BigDecimal lastEps) {
    this.lastEps = lastEps;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EarningsResponse earningsResponse = (EarningsResponse) o;
    return Objects.equals(this.symbol, earningsResponse.symbol) &&
        Objects.equals(this.frequency, earningsResponse.frequency) &&
        Objects.equals(this.rows, earningsResponse.rows) &&
        Objects.equals(this.nextEarningsDate, earningsResponse.nextEarningsDate) &&
        Objects.equals(this.lastEps, earningsResponse.lastEps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, frequency, rows, nextEarningsDate, lastEps);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EarningsResponse {\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    rows: ").append(toIndentedString(rows)).append("\n");
    sb.append("    nextEarningsDate: ").append(toIndentedString(nextEarningsDate)).append("\n");
    sb.append("    lastEps: ").append(toIndentedString(lastEps)).append("\n");
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

