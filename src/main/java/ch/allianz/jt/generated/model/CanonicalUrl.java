package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Canonical URL information for a news article.
 */

@Schema(name = "CanonicalUrl", description = "Canonical URL information for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class CanonicalUrl {

  private @Nullable String url = null;

  private @Nullable String site = null;

  private @Nullable String region = null;

  private @Nullable String lang = null;

  public CanonicalUrl url(@Nullable String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   */
  
  @Schema(name = "url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public @Nullable String getUrl() {
    return url;
  }

  public void setUrl(@Nullable String url) {
    this.url = url;
  }

  public CanonicalUrl site(@Nullable String site) {
    this.site = site;
    return this;
  }

  /**
   * Get site
   * @return site
   */
  
  @Schema(name = "site", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("site")
  public @Nullable String getSite() {
    return site;
  }

  public void setSite(@Nullable String site) {
    this.site = site;
  }

  public CanonicalUrl region(@Nullable String region) {
    this.region = region;
    return this;
  }

  /**
   * Get region
   * @return region
   */
  
  @Schema(name = "region", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("region")
  public @Nullable String getRegion() {
    return region;
  }

  public void setRegion(@Nullable String region) {
    this.region = region;
  }

  public CanonicalUrl lang(@Nullable String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Get lang
   * @return lang
   */
  
  @Schema(name = "lang", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lang")
  public @Nullable String getLang() {
    return lang;
  }

  public void setLang(@Nullable String lang) {
    this.lang = lang;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CanonicalUrl canonicalUrl = (CanonicalUrl) o;
    return Objects.equals(this.url, canonicalUrl.url) &&
        Objects.equals(this.site, canonicalUrl.site) &&
        Objects.equals(this.region, canonicalUrl.region) &&
        Objects.equals(this.lang, canonicalUrl.lang);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, site, region, lang);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CanonicalUrl {\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    site: ").append(toIndentedString(site)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
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

