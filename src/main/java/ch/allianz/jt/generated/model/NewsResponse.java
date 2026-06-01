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
 * News response for a symbol.
 */

@Schema(name = "NewsResponse", description = "News response for a symbol.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class NewsResponse {

  @Valid
  private List<@Valid NewsRow> news = new ArrayList<>();

  public NewsResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NewsResponse(List<@Valid NewsRow> news) {
    this.news = news;
  }

  public NewsResponse news(List<@Valid NewsRow> news) {
    this.news = news;
    return this;
  }

  public NewsResponse addNewsItem(NewsRow newsItem) {
    if (this.news == null) {
      this.news = new ArrayList<>();
    }
    this.news.add(newsItem);
    return this;
  }

  /**
   * Get news
   * @return news
   */
  @NotNull @Valid 
  @Schema(name = "news", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("news")
  public List<@Valid NewsRow> getNews() {
    return news;
  }

  public void setNews(List<@Valid NewsRow> news) {
    this.news = news;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsResponse newsResponse = (NewsResponse) o;
    return Objects.equals(this.news, newsResponse.news);
  }

  @Override
  public int hashCode() {
    return Objects.hash(news);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsResponse {\n");
    sb.append("    news: ").append(toIndentedString(news)).append("\n");
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

