package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Model for storing historical price data.
 */

@Schema(name = "HistoricalPrice", description = "Model for storing historical price data.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class HistoricalPrice {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate date;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  private BigDecimal open;

  private BigDecimal high;

  private BigDecimal low;

  private BigDecimal close;

  private Integer volume = null;

  public HistoricalPrice() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HistoricalPrice(LocalDate date, OffsetDateTime timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Integer volume) {
    this.date = date;
    this.timestamp = timestamp;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }

  public HistoricalPrice date(LocalDate date) {
    this.date = date;
    return this;
  }

  /**
   * Date of the price
   * @return date
   */
  @NotNull @Valid 
  @Schema(name = "date", description = "Date of the price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("date")
  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public HistoricalPrice timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * UTC timestamp of the price (corresponds to the date field with timezone information)
   * @return timestamp
   */
  @NotNull @Valid 
  @Schema(name = "timestamp", description = "UTC timestamp of the price (corresponds to the date field with timezone information)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public HistoricalPrice open(BigDecimal open) {
    this.open = open;
    return this;
  }

  /**
   * Opening price
   * @return open
   */
  @NotNull @Valid 
  @Schema(name = "open", description = "Opening price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("open")
  public BigDecimal getOpen() {
    return open;
  }

  public void setOpen(BigDecimal open) {
    this.open = open;
  }

  public HistoricalPrice high(BigDecimal high) {
    this.high = high;
    return this;
  }

  /**
   * Highest price
   * @return high
   */
  @NotNull @Valid 
  @Schema(name = "high", description = "Highest price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("high")
  public BigDecimal getHigh() {
    return high;
  }

  public void setHigh(BigDecimal high) {
    this.high = high;
  }

  public HistoricalPrice low(BigDecimal low) {
    this.low = low;
    return this;
  }

  /**
   * Lowest price
   * @return low
   */
  @NotNull @Valid 
  @Schema(name = "low", description = "Lowest price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("low")
  public BigDecimal getLow() {
    return low;
  }

  public void setLow(BigDecimal low) {
    this.low = low;
  }

  public HistoricalPrice close(BigDecimal close) {
    this.close = close;
    return this;
  }

  /**
   * Closing price
   * @return close
   */
  @NotNull @Valid 
  @Schema(name = "close", description = "Closing price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("close")
  public BigDecimal getClose() {
    return close;
  }

  public void setClose(BigDecimal close) {
    this.close = close;
  }

  public HistoricalPrice volume(Integer volume) {
    this.volume = volume;
    return this;
  }

  /**
   * Get volume
   * @return volume
   */
  @NotNull 
  @Schema(name = "volume", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("volume")
  public Integer getVolume() {
    return volume;
  }

  public void setVolume(Integer volume) {
    this.volume = volume;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HistoricalPrice historicalPrice = (HistoricalPrice) o;
    return Objects.equals(this.date, historicalPrice.date) &&
        Objects.equals(this.timestamp, historicalPrice.timestamp) &&
        Objects.equals(this.open, historicalPrice.open) &&
        Objects.equals(this.high, historicalPrice.high) &&
        Objects.equals(this.low, historicalPrice.low) &&
        Objects.equals(this.close, historicalPrice.close) &&
        Objects.equals(this.volume, historicalPrice.volume);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, timestamp, open, high, low, close, volume);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalPrice {\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    open: ").append(toIndentedString(open)).append("\n");
    sb.append("    high: ").append(toIndentedString(high)).append("\n");
    sb.append("    low: ").append(toIndentedString(low)).append("\n");
    sb.append("    close: ").append(toIndentedString(close)).append("\n");
    sb.append("    volume: ").append(toIndentedString(volume)).append("\n");
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

