package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;

import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * ResponseGetquotesbulkValue
 */

@JsonTypeName("Response_Getquotesbulk_value")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class ResponseGetquotesbulkValue {

  private String symbol;

  private BigDecimal currentPrice;

  private BigDecimal previousClose;

  private BigDecimal openPrice;

  private BigDecimal high;

  private BigDecimal low;

  private @Nullable Integer volume;

  private String error;

  private Integer statusCode;

  public ResponseGetquotesbulkValue() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ResponseGetquotesbulkValue(String symbol, BigDecimal currentPrice, BigDecimal previousClose, BigDecimal openPrice, BigDecimal high, BigDecimal low, String error, Integer statusCode) {
    this.symbol = symbol;
    this.currentPrice = currentPrice;
    this.previousClose = previousClose;
    this.openPrice = openPrice;
    this.high = high;
    this.low = low;
    this.error = error;
    this.statusCode = statusCode;
  }

  public ResponseGetquotesbulkValue symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Ticker symbol
   * @return symbol
   */
  @NotNull 
  @Schema(name = "symbol", description = "Ticker symbol", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("symbol")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public ResponseGetquotesbulkValue currentPrice(BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
    return this;
  }

  /**
   * Current market price
   * @return currentPrice
   */
  @NotNull @Valid 
  @Schema(name = "current_price", description = "Current market price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("current_price")
  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
  }

  public ResponseGetquotesbulkValue previousClose(BigDecimal previousClose) {
    this.previousClose = previousClose;
    return this;
  }

  /**
   * Previous closing price
   * @return previousClose
   */
  @NotNull @Valid 
  @Schema(name = "previous_close", description = "Previous closing price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("previous_close")
  public BigDecimal getPreviousClose() {
    return previousClose;
  }

  public void setPreviousClose(BigDecimal previousClose) {
    this.previousClose = previousClose;
  }

  public ResponseGetquotesbulkValue openPrice(BigDecimal openPrice) {
    this.openPrice = openPrice;
    return this;
  }

  /**
   * Opening price
   * @return openPrice
   */
  @NotNull @Valid 
  @Schema(name = "open_price", description = "Opening price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("open_price")
  public BigDecimal getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(BigDecimal openPrice) {
    this.openPrice = openPrice;
  }

  public ResponseGetquotesbulkValue high(BigDecimal high) {
    this.high = high;
    return this;
  }

  /**
   * Highest price of the day
   * @return high
   */
  @NotNull @Valid 
  @Schema(name = "high", description = "Highest price of the day", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("high")
  public BigDecimal getHigh() {
    return high;
  }

  public void setHigh(BigDecimal high) {
    this.high = high;
  }

  public ResponseGetquotesbulkValue low(BigDecimal low) {
    this.low = low;
    return this;
  }

  /**
   * Lowest price of the day
   * @return low
   */
  @NotNull @Valid 
  @Schema(name = "low", description = "Lowest price of the day", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("low")
  public BigDecimal getLow() {
    return low;
  }

  public void setLow(BigDecimal low) {
    this.low = low;
  }

  public ResponseGetquotesbulkValue volume(@Nullable Integer volume) {
    this.volume = volume;
    return this;
  }

  /**
   * Get volume
   * @return volume
   */
  
  @Schema(name = "volume", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("volume")
  public @Nullable Integer getVolume() {
    return volume;
  }

  public void setVolume(@Nullable Integer volume) {
    this.volume = volume;
  }

  public ResponseGetquotesbulkValue error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  @NotNull 
  @Schema(name = "error", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public ResponseGetquotesbulkValue statusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Get statusCode
   * @return statusCode
   */
  @NotNull 
  @Schema(name = "status_code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status_code")
  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResponseGetquotesbulkValue responseGetquotesbulkValue = (ResponseGetquotesbulkValue) o;
    return Objects.equals(this.symbol, responseGetquotesbulkValue.symbol) &&
        Objects.equals(this.currentPrice, responseGetquotesbulkValue.currentPrice) &&
        Objects.equals(this.previousClose, responseGetquotesbulkValue.previousClose) &&
        Objects.equals(this.openPrice, responseGetquotesbulkValue.openPrice) &&
        Objects.equals(this.high, responseGetquotesbulkValue.high) &&
        Objects.equals(this.low, responseGetquotesbulkValue.low) &&
        Objects.equals(this.volume, responseGetquotesbulkValue.volume) &&
        Objects.equals(this.error, responseGetquotesbulkValue.error) &&
        Objects.equals(this.statusCode, responseGetquotesbulkValue.statusCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, currentPrice, previousClose, openPrice, high, low, volume, error, statusCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResponseGetquotesbulkValue {\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    currentPrice: ").append(toIndentedString(currentPrice)).append("\n");
    sb.append("    previousClose: ").append(toIndentedString(previousClose)).append("\n");
    sb.append("    openPrice: ").append(toIndentedString(openPrice)).append("\n");
    sb.append("    high: ").append(toIndentedString(high)).append("\n");
    sb.append("    low: ").append(toIndentedString(low)).append("\n");
    sb.append("    volume: ").append(toIndentedString(volume)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
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

