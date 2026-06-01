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
 * Composite response containing both info and quote data for a symbol.
 */

@Schema(name = "SnapshotResponse", description = "Composite response containing both info and quote data for a symbol.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class SnapshotResponse {

  private String symbol;

  private InfoResponse info;

  private QuoteResponse quote;

  private @Nullable BigDecimal currentPrice = null;

  private @Nullable String currency = null;

  public SnapshotResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SnapshotResponse(String symbol, InfoResponse info, QuoteResponse quote) {
    this.symbol = symbol;
    this.info = info;
    this.quote = quote;
  }

  public SnapshotResponse symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Ticker symbol (e.g., AAPL)
   * @return symbol
   */
  @NotNull 
  @Schema(name = "symbol", description = "Ticker symbol (e.g., AAPL)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("symbol")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public SnapshotResponse info(InfoResponse info) {
    this.info = info;
    return this;
  }

  /**
   * Company information
   * @return info
   */
  @NotNull @Valid 
  @Schema(name = "info", description = "Company information", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("info")
  public InfoResponse getInfo() {
    return info;
  }

  public void setInfo(InfoResponse info) {
    this.info = info;
  }

  public SnapshotResponse quote(QuoteResponse quote) {
    this.quote = quote;
    return this;
  }

  /**
   * Current stock quote
   * @return quote
   */
  @NotNull @Valid 
  @Schema(name = "quote", description = "Current stock quote", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("quote")
  public QuoteResponse getQuote() {
    return quote;
  }

  public void setQuote(QuoteResponse quote) {
    this.quote = quote;
  }

  public SnapshotResponse currentPrice(@Nullable BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
    return this;
  }

  /**
   * Get currentPrice
   * @return currentPrice
   */
  @Valid 
  @Schema(name = "current_price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("current_price")
  public @Nullable BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(@Nullable BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
  }

  public SnapshotResponse currency(@Nullable String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public @Nullable String getCurrency() {
    return currency;
  }

  public void setCurrency(@Nullable String currency) {
    this.currency = currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SnapshotResponse snapshotResponse = (SnapshotResponse) o;
    return Objects.equals(this.symbol, snapshotResponse.symbol) &&
        Objects.equals(this.info, snapshotResponse.info) &&
        Objects.equals(this.quote, snapshotResponse.quote) &&
        Objects.equals(this.currentPrice, snapshotResponse.currentPrice) &&
        Objects.equals(this.currency, snapshotResponse.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, info, quote, currentPrice, currency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SnapshotResponse {\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    info: ").append(toIndentedString(info)).append("\n");
    sb.append("    quote: ").append(toIndentedString(quote)).append("\n");
    sb.append("    currentPrice: ").append(toIndentedString(currentPrice)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
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

