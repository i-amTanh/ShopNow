// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#292b2c';

// Area Chart Example
var ctx = document.getElementById("myAreaChart").getContext('2d');
var myLineChart = new Chart(ctx, {
  type: 'line',  // Chọn loại biểu đồ là line (đường)
  data: {
    labels: ["Mar 1", "Mar 2", "Mar 3", "Mar 4", "Mar 5", "Mar 6", "Mar 7", "Mar 8", "Mar 9", "Mar 10", "Mar 11", "Mar 12", "Mar 13"],  // Nhãn thời gian trên trục X
    datasets: [{
      label: "Sessions",  // Tên dữ liệu
      lineTension: 0.3,  // Độ căng của đường biểu đồ (tạo độ cong)
      backgroundColor: "rgba(2,117,216,0.2)",  // Màu nền bên dưới đường
      borderColor: "rgba(2,117,216,1)",  // Màu đường biểu đồ
      pointRadius: 5,  // Bán kính của điểm trên đường
      pointBackgroundColor: "rgba(2,117,216,1)",  // Màu điểm trên đường
      pointBorderColor: "rgba(255,255,255,0.8)",  // Màu viền điểm
      pointHoverRadius: 5,  // Bán kính khi hover
      pointHoverBackgroundColor: "rgba(2,117,216,1)",  // Màu nền khi hover
      pointHitRadius: 50,  // Tầm ảnh hưởng của điểm khi được nhấp
      pointBorderWidth: 2,  // Độ dày viền của điểm
      data: [10000, 30162, 26263, 18394, 18287, 28682, 31274, 33259, 25849, 24159, 32651, 31984, 38451],  // Dữ liệu trục Y
    }],
  },
  options: {
    scales: {
      xAxes: [{
        time: {
          unit: 'date'  // Đơn vị cho trục X là ngày
        },
        gridLines: {
          display: false  // Tắt các dòng lưới của trục X
        },
        ticks: {
          maxTicksLimit: 7  // Giới hạn số lượng ticks trên trục X
        }
      }],
      yAxes: [{
        ticks: {
          min: 0,  // Giá trị nhỏ nhất của trục Y
          max: 40000,  // Giá trị lớn nhất của trục Y
          maxTicksLimit: 5  // Giới hạn số lượng ticks trên trục Y
        },
        gridLines: {
          color: "rgba(0, 0, 0, .125)",  // Màu của các dòng lưới của trục Y
        }
      }],
    },
    legend: {
      display: false  // Ẩn biểu tượng chú thích của biểu đồ
    }
  }
});
