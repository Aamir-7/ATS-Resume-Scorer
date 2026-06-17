package com.ResumeScore.ATS.analysis.dashboard;

import com.ResumeScore.ATS.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService ;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashBoardSummaryResponse>>getSummary(

    ){
      DashBoardSummaryResponse response=dashBoardService.getSummary();
      return ResponseEntity.ok(
              ApiResponse.success("summary fetched success ",response)
      );
    }
}
