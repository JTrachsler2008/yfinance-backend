package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Finance information for a news article.
 */

@Schema(name = "Finance", description = "Finance information for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class Finance {

  private @Nullable PremiumFinance premiumFinance = null;

  public Finance premiumFinance(@Nullable PremiumFinance premiumFinance) {
    this.premiumFinance = premiumFinance;
    return this;
  }

  /**
   * Get premiumFinance
   * @return premiumFinance
   */
  @Valid 
  @Schema(name = "premiumFinance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("premiumFinance")
  public @Nullable PremiumFinance getPremiumFinance() {
    return premiumFinance;
  }

  public void setPremiumFinance(@Nullable PremiumFinance premiumFinance) {
    this.premiumFinance = premiumFinance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Finance finance = (Finance) o;
    return Objects.equals(this.premiumFinance, finance.premiumFinance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(premiumFinance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Finance {\n");
    sb.append("    premiumFinance: ").append(toIndentedString(premiumFinance)).append("\n");
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

