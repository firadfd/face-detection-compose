package fd.firad.face.core.network

object ApiEndpoints {
    private const val BASE_URL = "https://attendance-kappa-nine.vercel.app/api"

    // Employees
    const val REGISTER_EMPLOYEE = "$BASE_URL/employees/register"
    const val LIST_EMPLOYEES = "$BASE_URL/employees"
    fun deleteEmployee(employeeId: String) = "$BASE_URL/employees/$employeeId"

    // Attendance
    const val LOG_ATTENDANCE = "$BASE_URL/attendance/log"
    fun dailyAttendance(date: String) = "$BASE_URL/attendance/daily/$date"
    fun employeeHistory(employeeId: String) = "$BASE_URL/attendance/employee/$employeeId"
    fun employeeStatistics(employeeId: String, range: String) =
        "$BASE_URL/attendance/status/$employeeId?range=$range"
    fun deleteAttendanceEntry(id: String) = "$BASE_URL/attendance/$id"
}
