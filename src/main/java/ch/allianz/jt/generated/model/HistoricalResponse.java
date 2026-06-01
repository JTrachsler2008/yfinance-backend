package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Response model for historical stock data.
 */

@Schema(name = "HistoricalResponse", description = "Response model for historical stock data.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class HistoricalResponse {

  private String symbol;

  @Valid
  private List<@Valid HistoricalPrice> prices = new ArrayList<>();

  public HistoricalResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HistoricalResponse(String symbol, List<@Valid HistoricalPrice> prices) {
    this.symbol = symbol;
    this.prices = prices;
  }

  public HistoricalResponse symbol(String symbol) {
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

  public HistoricalResponse prices(List<@Valid HistoricalPrice> prices) {
    this.prices = prices;
    return this;
  }

  public HistoricalResponse addPricesItem(HistoricalPrice pricesItem) {
    if (this.prices == null) {
      this.prices = new ArrayList<>();
    }
    this.prices.add(pricesItem);
    return this;
  }

  /**
   * List of historical prices
   * @return prices
   */
  @NotNull @Valid 
  @Schema(name = "prices", description = "List of historical prices", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("prices")
  public List<@Valid HistoricalPrice> getPrices() {
    return prices;
  }

  public void setPrices(List<@Valid HistoricalPrice> prices) {
    this.prices = prices;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HistoricalResponse historicalResponse = (HistoricalResponse) o;
    return Objects.equals(this.symbol, historicalResponse.symbol) &&
        Objects.equals(this.prices, historicalResponse.prices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, prices);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalResponse {\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    prices: ").append(toIndentedString(prices)).append("\n");
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

