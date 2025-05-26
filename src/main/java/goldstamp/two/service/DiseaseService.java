package goldstamp.two.service;

import goldstamp.two.domain.Disease;
import goldstamp.two.repository.DiseaseRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class DiseaseService {

    @Autowired
    DiseaseRepository diseaseRepository;

    public void saveDiseases() throws Exception {
        disableSslVerification();
        List<Disease> diseases = new ArrayList<>();
        //List<Integer> check = new ArrayList<>();
        List<Integer> passedNumbers = Arrays.asList(
                6, 176, 1041, 1061, 1081, 1101, 1102, 1103, 1104, 1141, 1201, 1241, 1261, 1262, 1263, 1264, 1265, 1266, 1281, 1301,
                1322, 1342, 1344, 1361, 1362, 1381, 1382, 1401, 1402, 1403, 1404, 1469, 1567, 1587, 1607, 1608, 1667, 1729, 1738, 1741,
                1743, 1746, 1747, 1750, 1751, 1807, 1831, 1988, 2028, 2047, 2048, 2049, 2050, 2052, 2055, 2057, 2067, 2087, 2107, 2247,
                2347, 2348, 2349, 2350, 2351, 2367, 2387, 2388, 2389, 2390, 2391, 2392, 2393, 2447, 2552, 2587, 2607, 2687, 2727, 2847,
                2967, 2968, 2969, 2970, 2971, 3012, 3134, 3135, 3136, 3167, 3193, 3329, 3331, 3338, 3348, 3388, 3390, 3430, 3432, 3433,
                3434, 3435, 3436, 3447, 3467, 3508, 3509, 3512, 3547, 3568, 3607, 3627, 3628, 3629, 3690, 3727, 3728, 3749, 3768, 3789,
                3790, 3791, 3792, 3793, 3794, 3795, 3796, 3827, 3828, 3848, 3947, 3987, 4008, 4027, 4029, 4030, 4047, 4516, 4576, 4635,
                5211, 5212, 5213, 5214, 5215, 5217, 5219, 5220, 5221, 5222, 5223, 5224, 5226, 5227, 5228, 5229, 5230, 5231, 5232, 5233,
                5234, 5235, 5236, 5237, 5238, 5239, 5240, 5241, 5242, 5243, 5245, 5246, 5247, 5248, 5249, 5250, 5251, 5252, 5253, 5254,
                5255, 5257, 5258, 5259, 5260, 5261, 5263, 5264, 5265, 5266, 5267, 5268, 5269, 5270, 5271, 5279, 5280, 5281, 5282, 5283,
                5284, 5285, 5286, 5287, 5288, 5289, 5290, 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303,
                5304, 5305, 5306, 5307, 5308, 5309, 5310, 5311, 5314, 5316, 5317, 5318, 5325, 5326, 5329, 5330, 5332, 5333, 5334, 5335,
                5336, 5337, 5338, 5339, 5340, 5341, 5343, 5345, 5346, 5347, 5350, 5351, 5353, 5354, 5355, 5357, 5359, 5361, 5362, 5364,
                5365, 5366, 5370, 5371, 5372, 5407, 5408, 5410, 5411, 5412, 5413, 5415, 5416, 5419, 5420, 5422, 5423, 5427, 5428, 5429,
                5430, 5432, 5433, 5434, 5439, 5441, 5443, 5444, 5445, 5446, 5447, 5457, 5460, 5462, 5463, 5466, 5467, 5468, 5470, 5471,
                5472, 5473, 5474, 5479, 5480, 5481, 5482, 5483, 5485, 5486, 5487, 5488, 5489, 5490, 5491, 5492, 5493, 5494, 5495, 5498,
                5499, 5500, 5503, 5504, 5505, 5506, 5507, 5508, 5509, 5511, 5513, 5514, 5515, 5516, 5517, 5518, 5521, 5522, 5523, 5524,
                5525, 5526, 5527, 5528, 5529, 5530, 5531, 5532, 5533, 5535, 5536, 5537, 5538, 5539, 5540, 5543, 5545, 5679, 5680, 5681,
                5682, 5683, 5684, 5685, 5686, 5687, 5688, 5689, 5690, 5691, 5692, 5693, 5694, 5695, 5696, 5697, 5698, 5699, 5700, 5701,
                5702, 5703, 5704, 5705, 5706, 5707, 5708, 5709, 5710, 5711, 5712, 5713, 5714, 5715, 5716, 5717, 5718, 5719, 5720, 5721,
                5722, 5723, 5724, 5725, 5726, 5727, 5728, 5729, 5779, 5782, 5783, 5796, 5800, 5801, 5803, 5804, 5806, 5807, 5809, 5811,
                5812, 5813, 5814, 5815, 5817, 5818, 5820, 5821, 5822, 5823, 5824, 5826, 5827, 5829, 5830, 5831, 5833, 5834, 5835, 5837,
                5838, 5840, 5841, 5842, 5843, 5844, 5845, 5846, 5847, 5848, 5849, 5850, 5851, 5852, 5853, 5854, 5856, 5857, 5858, 5859,
                5860, 5862, 5863, 5864, 5865, 5919, 5960, 5961, 5962, 5963, 5964, 5968, 5969, 5971, 5972, 5975, 5999, 6013, 6054, 6154,
                6188, 6189, 6190, 6226, 6227, 6228, 6240, 6241, 6242, 6243, 6244, 6246, 6247, 6248, 6249, 6250, 6251, 6252, 6253, 6254,
                6255, 6256, 6257, 6258, 6259, 6260, 6261, 6262, 6263, 6264, 6266, 6267, 6268, 6269, 6270, 6271, 6272, 6275, 6276, 6277,
                6278, 6279, 6280, 6282, 6283, 6288, 6289, 6290, 6291, 6292, 6293, 6294, 6295, 6298, 6300, 6301, 6302, 6303, 6304, 6305,
                6306, 6307, 6308, 6309, 6310, 6311, 6312, 6313, 6314, 6315, 6316, 6317, 6318, 6319, 6320, 6321, 6322, 6386, 6388, 6389,
                6391, 6437, 6442, 6443, 6444, 6445, 6449, 6450, 6451, 6454, 6455, 6456, 6457, 6458, 6459, 6465, 6466, 6467, 6468, 6470,
                6471, 6472, 6474, 6475, 6478, 6479, 6480, 6481, 6482, 6483, 6484, 6485, 6486, 6487, 6488, 6489, 6490, 6491, 6492, 6493,
                6494, 6510, 6524, 6525, 6526, 6527, 6528, 6529, 6530, 6531, 6532, 6533, 6534, 6535, 6536, 6537, 6538, 6539, 6540, 6541,
                6542, 6543, 6544, 6545, 6546, 6548, 6549, 6550, 6551, 6552, 6553, 6555, 6556, 6557, 6558, 6559, 6560, 6561, 6562, 6563,
                6564, 6565, 6566, 6567, 6568, 6569, 6570, 6571, 6572, 6574, 6575, 6576, 6577, 6578, 6580, 6581, 6582, 6583, 6584, 6585,
                6586, 6613, 6615, 6616, 6618, 6619, 6620, 6621, 6622, 6623, 6624
        );

        for (Integer number : passedNumbers) { //int number = 1; number <= 7000; number++
            String urlString = "https://api.kdca.go.kr/api/provide/healthInfo?TOKEN=195fddf461d5&cntntsSn=" + number;
            try (InputStream inputStream = new URL(urlString).openStream()) {
                Document doc = Jsoup.parse(inputStream, "UTF-8", "", Parser.xmlParser());

                Element cntntssj = doc.selectFirst("CNTNTSSJ");
                String name = cntntssj != null ? cntntssj.text().trim() : "";

                Element contentElement = doc.selectFirst("CNTNTS_CL_CN");
                String explain = contentElement != null ? contentElement.text().trim() : "";

                if (!name.isEmpty() && !explain.isEmpty()) {
                    if (explain.getBytes(StandardCharsets.UTF_8).length <= 3000) {
                        Disease disease = new Disease();
                        disease.setName(name);
                        disease.setExplain(explain);
                        diseases.add(disease);
                        System.out.println("Parsed: " + number);
                    } else {
                        System.out.println("Skipped (too long): " + number + " | length=" + explain.length());
                    }
                } else {
                    System.out.println("No content for contentSn: " + number);
                }

            } catch (Exception e) {
                System.out.println("Failed to parse contentSn: " + number + " - " + e.getMessage());
            }
        }
        //루프 끝난 후 한 번만 저장
        //System.out.println("통과한 번호: " + check);
        diseaseRepository.saveAll(diseases);
        System.out.println("Saved items to DB: " + diseases.size());
    }
    // SSL 검증 우회 메서드
    private static void disableSslVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
