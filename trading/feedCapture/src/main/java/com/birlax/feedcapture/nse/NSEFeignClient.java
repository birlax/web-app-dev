package com.birlax.feedcapture.nse;

import com.birlax.feedcapture.config.CustomFeignClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(
    name = "NseDataSync",
    url = "https://www.nseindia.com",
    configuration = CustomFeignClientConfig.class)
@Service
public interface NSEFeignClient {

  // https://www.nseindia.com/api/historical/securityArchives
  // ?from=11-02-2023
  // &to=11-02-2024
  // &symbol=INFY
  // &dataType=priceVolumeDeliverable
  // &series=EQ
  // &csv=true
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/api/historical/securityArchives?dataType=priceVolumeDeliverable&series=EQ&csv=true")
  // @Headers({
  // "authority: www.nseindia.com",
  // "accept: application/json",
  // "accept-language: en-GB,en-US;q=0.9,en;q=0.8",
  // , "cookie: _ga=GA1.1.77961335.1706962899; AKA_A2=A; nsit=uzxQ4MWcBV1okzNFa84gB2FH;
  // bm_mi=7BEC42459157B4631F0ED6DE79D489BD~YAAQNNjIFyxcCmKNAQAAC1ZZ2hYNVRf6SoJpmHrEkYpEAL/7Vde54wT/hEzbD1BLxWyaj0qVl90IjCn4s3ZltEaX3vie0/14TxAt0w9/VJ74OIJAeNGUNnwWyg7RxFZ9x4I0zz7VdSfALtvlR+TrhXMOIUZDfpQBxAdCTyvHbWJ4PLLbRQXSCy69cZPTawWz4W9mDes6Q6UIBpaC1lHo2Ajz+XEsEcRJGXQ6AtrjKUSD3HpuHOJ7g9VT+FurvePw4bDDBivMLgWGcqymY4eQxfCqHly9pSDEZXT5Qrgmxk+QPOgxEiAEuX/MGW4zr9cx~1; defaultLang=en; ak_bmsc=9D8C564E64934BD1215D870371ED6939~000000000000000000000000000000~YAAQNNjIFzdcCmKNAQAAgVpZ2haVOd8ShDsrjvnEQwCZ07iltC+N0siErzHLRjubihXB2+Iyz/Dim6t3CHu+FhGdeQGSxeuXyGMru3Cs7T52K6oFncorcAH/oI4qfmkQt2+KzL/qCEpKI639YG32ZoPgoXvaXikfBjT0YrJLfV4FJvQ3CHffI+5tV2zDXm3kb7FDOC2TCP4LMKa3LdWSa5mf7eiPeNXBYymozXZ7TZnaSHx05yQi/lOYCKDTBP37Gk+SySwyVu4VTMPX6gnxlSA98GG0KyFJvkpPxVdBUCIJmrGPGW988obtceQHu2gc2W+kj1EsKmWtRHV16cK9SmLabkoCuUR68y3SaOtO1A52WuNmPTt1yQymKayCnDYIeULBvUrvwlJCxWusHMUtgriR7tBjWAMq3tWjiErRAh+FmbuzlrlUVfNVoVyHz/4inE7Lj1FyNbRfri3FOOD3HstLxmkZ4p4s04c2auiIYl9sAhS7e2BgeonwqFgrZHEZq9PQX4eM1vXTqQEzF/AkT+a43Cpp; nseappid=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcGkubnNlIiwiYXVkIjoiYXBpLm5zZSIsImlhdCI6MTcwODc2NTM1MSwiZXhwIjoxNzA4NzcyNTUxfQ.92tVqi63YcNdexPYhXmir7K7k9q8pGuPB1KJVRYGCCk; _ga_QJZ4447QD3=GS1.1.1708765326.16.1.1708765372.0.0.0; _ga_87M7PJ3R97=GS1.1.1708765305.19.1.1708765372.0.0.0; bm_sv=49C2DB3EE93A75D6146CC289BD54A5A4~YAAQNNjIFyBnCmKNAQAAwLxb2hayS4SGXq+7TJqsONy0yedLOX470BOmtWItpujrMD5C/q5QOnLo9vMqiOT0FQwWO0FiOSYdrZyNM3cnHQ7jrUVSrhFjP4ns8wnKlb0OdBeQbHJDivjzmispmj+lFZ4LcoG7HztGCG/yADXMqFNHcQYiuhy3GgUYIlqG0m+0QPVmKIv69VZSOgpGhAWhe9KwIjWAqEm36FeyhtMAdNsG8nhb6BQC9+eyK7h9G94FOlG/~1; RT="z=1&dm=nseindia.com&si=587b9ff5-8264-4303-8627-5ced7eefc1f1&ss=lszuqqzv&sl=3&se=8c&tt=6yf&bcn=%2F%2F684d0d41.akstat.io%2F&ld=16ll&nu=kpaxjfo&cl=3gye'"
  // "referer: https://www.nseindia.com/report-detail/eq_security",
  //  "sec-ch-ua: \"Not A(Brand\";v=\"99\", \"Google Chrome\";v=\"121\", \"Chromium\";v=\"121\""
  // "sec-ch-ua-mobile: ?0",
  // , "sec-ch-ua-platform: \"macOS\""
  // "sec-fetch-dest: empty",
  // "sec-fetch-mode: cors",
  // "sec-fetch-site: same-origin",
  // "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like
  // Gecko) Chrome/121.0.0.0 Safari/537.36",
  // "x-requested-with: XMLHttpRequest"
  // })
  @Headers({
          "Content-Type: application/json",
        //  "Authorization: Bearer {accessToken}" // Use parameterized header
  })
  String fetchData(
      @RequestParam("symbol") String symbol,
      @RequestParam("fromDate") String from,
      @RequestParam("toDate") String to);
}
