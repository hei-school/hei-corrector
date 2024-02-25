package school.hei.corrector.sarisary;

public class BlackAndWhite {

  private String original_url;
  private String transformed_url;

  public String getOriginal_url() {
    return original_url;
  }

  public void setOriginal_url(String original_url) {
    this.original_url = original_url;
  }

  public String getTransformed_url() {
    return transformed_url;
  }

  public void setTransformed_url(String transformed_url) {
    this.transformed_url = transformed_url;
  }

  @Override
  public String toString() {
    return "BlackAndWhite{" +
        "original_url='" + original_url + '\'' +
        ", transformed_url='" + transformed_url + '\'' +
        '}';
  }
}
