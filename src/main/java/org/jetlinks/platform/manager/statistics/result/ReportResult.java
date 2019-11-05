//package org.jetlinks.platform.manager.statistics.result;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author bsetfeng
// * @since 1.0
// **/
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class ReportResult {
//
//    private List<String> fields;
//
//    private List<Integer> sums;
//
//    private List<Object> data;
//
//    public static TemporaryReportResult parser(List<TemporarySimpleCycleStatisticsResult> results, Comparator<? super TemporarySimpleCycleStatisticsResult> comparator) {
//        List<Object> _data = new ArrayList<>();
//        List<String> fields = new ArrayList<>();
//        List<Integer> fieldCountSums = new ArrayList<>();
//        Set<String> types = new HashSet<>();
//        List<Map<String, Integer>> dataDetails = results.stream()
//                .sorted(Comparator.comparing(TemporarySimpleCycleStatisticsResult::getCycleName))
//                .map(result -> {
//                    fields.add(result.getCycleName());
//                    fieldCountSums.add(result.getSum());
//                    Map<String, Integer> m = new HashMap<>();
//                    List<String> tempFields = result.getReportResult().getFields();
//                    List<Integer> tempSum = result.getReportResult().getSums();
//                    for (int i = 0; i < tempFields.size(); i++) {
//                        types.add(tempFields.get(i));
//                        m.put(tempFields.get(i), tempSum.get(i));
//                    }
//                    return m;
//                })
//                .collect(Collectors.toList());
//
//        types.forEach(s -> {
//            Map<String, Object> dataMap = new HashMap<>();
//            List<Object> counts = new ArrayList<>();
//            dataDetails.forEach(m -> counts.add(m.get(s) == null ? 0 : m.get(s)));
//            dataMap.put("type", s);
//            dataMap.put("data", counts);
//            _data.add(dataMap);
//        });
//        return TemporaryReportResult.builder()
//                .data(_data)
//                .fields(fields)
//                .sums(fieldCountSums)
//                .build();
//    }
//}
