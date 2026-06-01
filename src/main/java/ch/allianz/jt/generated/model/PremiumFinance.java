package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Premium finance information for a news article.
 */

@Schema(name = "PremiumFinance", description = "Premium finance information for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class PremiumFinance {

  private @Nullable Boolean isPremiumNews = null;

  private @Nullable Boolean isPremiumFreeNews = null;

  public PremiumFinance isPremiumNews(@Nullable Boolean isPremiumNews) {
    this.isPremiumNews = isPremiumNews;
    return this;
  }

  /**
   * Get isPremiumNews
   * @return isPremiumNews
   */
  
  @Schema(name = "isPremiumNews", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isPremiumNews")
  public @Nullable Boolean getIsPremiumNews() {
    return isPremiumNews;
  }

  public void setIsPremiumNews(@Nullable Boolean isPremiumNews) {
    this.isPremiumNews = isPremiumNews;
  }

  public PremiumFinance isPremiumFreeNews(@Nullable Boolean isPremiumFreeNews) {
    this.isPremiumFreeNews = isPremiumFreeNews;
    return this;
  }

  /**
   * Get isPremiumFreeNews
   * @return isPremiumFreeNews
   */
  
  @Schema(name = "isPremiumFreeNews", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isPremiumFreeNews")
  public @Nullable Boolean getIsPremiumFreeNews() {
    return isPremiumFreeNews;
  }

  public void setIsPremiumFreeNews(@Nullable Boolean isPremiumFreeNews) {
    this.isPremiumFreeNews = isPremiumFreeNews;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PremiumFinance premiumFinance = (PremiumFinance) o;
    return Objects.equals(this.isPremiumNews, premiumFinance.isPremiumNews) &&
        Objects.equals(this.isPremiumFreeNews, premiumFinance.isPremiumFreeNews);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isPremiumNews, isPremiumFreeNews);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PremiumFinance {\n");
    sb.append("    isPremiumNews: ").append(toIndentedString(isPremiumNews)).append("\n");
    sb.append("    isPremiumFreeNews: ").append(toIndentedString(isPremiumFreeNews)).append("\n");
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

