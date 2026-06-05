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
 * Model for storing information about the application.
 */

@Schema(name = "InfoResponse", description = "Model for storing information about the application.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class InfoResponse {

  private String symbol;

  private @Nullable String shortName = null;

  private @Nullable String longName = null;

  private @Nullable String exchange = null;

  private @Nullable String sector = null;

  private @Nullable String industry = null;

  private @Nullable String country = null;

  private @Nullable String website = null;

  private @Nullable String description = null;

  private @Nullable Long marketCap = null;

  private @Nullable Long sharesOutstanding = null;

  private @Nullable BigDecimal dividendYield = null;

  private @Nullable BigDecimal fiftyTwoWeekHigh = null;

  private @Nullable BigDecimal fiftyTwoWeekLow = null;

  private @Nullable BigDecimal currentPrice = null;

  private @Nullable BigDecimal trailingPe = null;

  private @Nullable BigDecimal beta = null;

  private @Nullable String address = null;

  private @Nullable String currency = null;

  public InfoResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public InfoResponse(String symbol) {
    this.symbol = symbol;
  }

  public InfoResponse symbol(String symbol) {
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

  public InfoResponse shortName(@Nullable String shortName) {
    this.shortName = shortName;
    return this;
  }

  /**
   * Get shortName
   * @return shortName
   */
  
  @Schema(name = "short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("short_name")
  public @Nullable String getShortName() {
    return shortName;
  }

  public void setShortName(@Nullable String shortName) {
    this.shortName = shortName;
  }

  public InfoResponse longName(@Nullable String longName) {
    this.longName = longName;
    return this;
  }

  /**
   * Get longName
   * @return longName
   */
  
  @Schema(name = "long_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("long_name")
  public @Nullable String getLongName() {
    return longName;
  }

  public void setLongName(@Nullable String longName) {
    this.longName = longName;
  }

  public InfoResponse exchange(@Nullable String exchange) {
    this.exchange = exchange;
    return this;
  }

  /**
   * Get exchange
   * @return exchange
   */
  
  @Schema(name = "exchange", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exchange")
  public @Nullable String getExchange() {
    return exchange;
  }

  public void setExchange(@Nullable String exchange) {
    this.exchange = exchange;
  }

  public InfoResponse sector(@Nullable String sector) {
    this.sector = sector;
    return this;
  }

  /**
   * Get sector
   * @return sector
   */
  
  @Schema(name = "sector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sector")
  public @Nullable String getSector() {
    return sector;
  }

  public void setSector(@Nullable String sector) {
    this.sector = sector;
  }

  public InfoResponse industry(@Nullable String industry) {
    this.industry = industry;
    return this;
  }

  /**
   * Get industry
   * @return industry
   */
  
  @Schema(name = "industry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("industry")
  public @Nullable String getIndustry() {
    return industry;
  }

  public void setIndustry(@Nullable String industry) {
    this.industry = industry;
  }

  public InfoResponse country(@Nullable String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  
  @Schema(name = "country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public @Nullable String getCountry() {
    return country;
  }

  public void setCountry(@Nullable String country) {
    this.country = country;
  }

  public InfoResponse website(@Nullable String website) {
    this.website = website;
    return this;
  }

  /**
   * Get website
   * @return website
   */
  
  @Schema(name = "website", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("website")
  public @Nullable String getWebsite() {
    return website;
  }

  public void setWebsite(@Nullable String website) {
    this.website = website;
  }

  public InfoResponse description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public InfoResponse marketCap(@Nullable Long marketCap) {
    this.marketCap = marketCap;
    return this;
  }

  /**
   * Get marketCap
   * @return marketCap
   */
  
  @Schema(name = "market_cap", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("market_cap")
  public @Nullable Long getMarketCap() {
    return marketCap;
  }

  public void setMarketCap(@Nullable Long marketCap) {
    this.marketCap = marketCap;
  }

  public InfoResponse sharesOutstanding(@Nullable Long sharesOutstanding) {
    this.sharesOutstanding = sharesOutstanding;
    return this;
  }

  /**
   * Get sharesOutstanding
   * @return sharesOutstanding
   */
  
  @Schema(name = "shares_outstanding", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shares_outstanding")
  public @Nullable Long getSharesOutstanding() {
    return sharesOutstanding;
  }

  public void setSharesOutstanding(@Nullable Long sharesOutstanding) {
    this.sharesOutstanding = sharesOutstanding;
  }

  public InfoResponse dividendYield(@Nullable BigDecimal dividendYield) {
    this.dividendYield = dividendYield;
    return this;
  }

  /**
   * Get dividendYield
   * @return dividendYield
   */
  @Valid 
  @Schema(name = "dividend_yield", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dividend_yield")
  public @Nullable BigDecimal getDividendYield() {
    return dividendYield;
  }

  public void setDividendYield(@Nullable BigDecimal dividendYield) {
    this.dividendYield = dividendYield;
  }

  public InfoResponse fiftyTwoWeekHigh(@Nullable BigDecimal fiftyTwoWeekHigh) {
    this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    return this;
  }

  /**
   * Get fiftyTwoWeekHigh
   * @return fiftyTwoWeekHigh
   */
  @Valid 
  @Schema(name = "fifty_two_week_high", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fifty_two_week_high")
  public @Nullable BigDecimal getFiftyTwoWeekHigh() {
    return fiftyTwoWeekHigh;
  }

  public void setFiftyTwoWeekHigh(@Nullable BigDecimal fiftyTwoWeekHigh) {
    this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
  }

  public InfoResponse fiftyTwoWeekLow(@Nullable BigDecimal fiftyTwoWeekLow) {
    this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    return this;
  }

  /**
   * Get fiftyTwoWeekLow
   * @return fiftyTwoWeekLow
   */
  @Valid 
  @Schema(name = "fifty_two_week_low", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fifty_two_week_low")
  public @Nullable BigDecimal getFiftyTwoWeekLow() {
    return fiftyTwoWeekLow;
  }

  public void setFiftyTwoWeekLow(@Nullable BigDecimal fiftyTwoWeekLow) {
    this.fiftyTwoWeekLow = fiftyTwoWeekLow;
  }

  public InfoResponse currentPrice(@Nullable BigDecimal currentPrice) {
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

  public InfoResponse trailingPe(@Nullable BigDecimal trailingPe) {
    this.trailingPe = trailingPe;
    return this;
  }

  /**
   * Get trailingPe
   * @return trailingPe
   */
  @Valid 
  @Schema(name = "trailing_pe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("trailing_pe")
  public @Nullable BigDecimal getTrailingPe() {
    return trailingPe;
  }

  public void setTrailingPe(@Nullable BigDecimal trailingPe) {
    this.trailingPe = trailingPe;
  }

  public InfoResponse beta(@Nullable BigDecimal beta) {
    this.beta = beta;
    return this;
  }

  /**
   * Get beta
   * @return beta
   */
  @Valid 
  @Schema(name = "beta", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("beta")
  public @Nullable BigDecimal getBeta() {
    return beta;
  }

  public void setBeta(@Nullable BigDecimal beta) {
    this.beta = beta;
  }

  public InfoResponse address(@Nullable String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public @Nullable String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    this.address = address;
  }

  public InfoResponse currency(@Nullable String currency) {
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
    InfoResponse infoResponse = (InfoResponse) o;
    return Objects.equals(this.symbol, infoResponse.symbol) &&
        Objects.equals(this.shortName, infoResponse.shortName) &&
        Objects.equals(this.longName, infoResponse.longName) &&
        Objects.equals(this.exchange, infoResponse.exchange) &&
        Objects.equals(this.sector, infoResponse.sector) &&
        Objects.equals(this.industry, infoResponse.industry) &&
        Objects.equals(this.country, infoResponse.country) &&
        Objects.equals(this.website, infoResponse.website) &&
        Objects.equals(this.description, infoResponse.description) &&
        Objects.equals(this.marketCap, infoResponse.marketCap) &&
        Objects.equals(this.sharesOutstanding, infoResponse.sharesOutstanding) &&
        Objects.equals(this.dividendYield, infoResponse.dividendYield) &&
        Objects.equals(this.fiftyTwoWeekHigh, infoResponse.fiftyTwoWeekHigh) &&
        Objects.equals(this.fiftyTwoWeekLow, infoResponse.fiftyTwoWeekLow) &&
        Objects.equals(this.currentPrice, infoResponse.currentPrice) &&
        Objects.equals(this.trailingPe, infoResponse.trailingPe) &&
        Objects.equals(this.beta, infoResponse.beta) &&
        Objects.equals(this.address, infoResponse.address) &&
        Objects.equals(this.currency, infoResponse.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, shortName, longName, exchange, sector, industry, country, website, description, marketCap, sharesOutstanding, dividendYield, fiftyTwoWeekHigh, fiftyTwoWeekLow, currentPrice, trailingPe, beta, address, currency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoResponse {\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    longName: ").append(toIndentedString(longName)).append("\n");
    sb.append("    exchange: ").append(toIndentedString(exchange)).append("\n");
    sb.append("    sector: ").append(toIndentedString(sector)).append("\n");
    sb.append("    industry: ").append(toIndentedString(industry)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    marketCap: ").append(toIndentedString(marketCap)).append("\n");
    sb.append("    sharesOutstanding: ").append(toIndentedString(sharesOutstanding)).append("\n");
    sb.append("    dividendYield: ").append(toIndentedString(dividendYield)).append("\n");
    sb.append("    fiftyTwoWeekHigh: ").append(toIndentedString(fiftyTwoWeekHigh)).append("\n");
    sb.append("    fiftyTwoWeekLow: ").append(toIndentedString(fiftyTwoWeekLow)).append("\n");
    sb.append("    currentPrice: ").append(toIndentedString(currentPrice)).append("\n");
    sb.append("    trailingPe: ").append(toIndentedString(trailingPe)).append("\n");
    sb.append("    beta: ").append(toIndentedString(beta)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
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

