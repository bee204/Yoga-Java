const http = require('http');
const querystring = require('querystring');
const { execSync } = require('child_process');

function request(options, postData = null) {
    return new Promise((resolve, reject) => {
        const req = http.request({
            hostname: 'localhost',
            port: 8080,
            path: options.path,
            method: options.method || 'GET',
            headers: {
                ...(options.cookie ? { 'Cookie': options.cookie } : {}),
                ...(postData ? {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Content-Length': Buffer.byteLength(postData)
                } : {})
            }
        }, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                const setCookie = res.headers['set-cookie'];
                const cookie = setCookie ? setCookie[0].split(';')[0] : null;
                resolve({
                    statusCode: res.statusCode,
                    headers: res.headers,
                    location: res.headers['location'],
                    cookie: cookie,
                    body: data
                });
            });
        });

        req.on('error', reject);
        if (postData) req.write(postData);
        req.end();
    });
}

function queryDb(sql) {
    try {
        const out = execSync(`C:\\xampp\\mysql\\bin\\mysql.exe -u root --default-character-set=utf8mb4 -e "${sql}" yoga_management`, { encoding: 'utf8' });
        return out.trim();
    } catch(e) {
        return null;
    }
}

async function runTests() {
    console.log('======================================================================');
    console.log('🚀 BẮT ĐẦU BỘ KIỂM THỬ TỰ ĐỘNG TOÀN DIỆN VITA YOGA (SPRING BOOT 3)');
    console.log('======================================================================\n');

    let passed = 0;
    let failed = 0;

    function assert(condition, message) {
        if (condition) {
            console.log(`  ✅ [PASS] ${message}`);
            passed++;
        } else {
            console.error(`  ❌ [FAIL] ${message}`);
            failed++;
        }
    }

    // -------------------------------------------------------------
    // BƯỚC 1: ĐĂNG NHẬP ADMIN & HỌC VIÊN
    // -------------------------------------------------------------
    console.log('--- 1. XÁC THỰC TÀI KHOẢN (AUTHENTICATION) ---');
    const adminLoginRes = await request({
        path: '/admin/login',
        method: 'POST'
    }, querystring.stringify({ username: 'admin', password: '123456' }));
    const adminCookie = adminLoginRes.cookie;
    assert(adminLoginRes.statusCode === 302 && adminCookie, 'Đăng nhập Quản trị viên (admin/123456) thành công');

    const studentLoginRes = await request({
        path: '/account/login',
        method: 'POST'
    }, querystring.stringify({ username: 'student1', password: '123456' }));
    const studentCookie = studentLoginRes.cookie;
    assert(studentLoginRes.statusCode === 302 && studentCookie, 'Đăng nhập Học viên (student1/123456) thành công');

    // -------------------------------------------------------------
    // BƯỚC 2: KIỂM THỬ DASHBOARD ADMIN VỚI BỘ LỌC NGÀY
    // -------------------------------------------------------------
    console.log('\n--- 2. DASHBOARD ADMIN & BỘ LỌC THỜI GIAN (DATE FILTERS) ---');
    const dashAll = await request({ path: '/admin/dashboard?range=all', cookie: adminCookie });
    assert(dashAll.statusCode === 200 && dashAll.body.includes('Lọc theo thời gian'), 'Dashboard tải thành công với tùy chọn Tất cả thời gian');

    const dashToday = await request({ path: '/admin/dashboard?range=today', cookie: adminCookie });
    assert(dashToday.statusCode === 200 && dashToday.body.includes('Đang hiển thị thống kê theo kỳ lọc'), 'Dashboard lọc thời gian thực theo Hôm nay (today)');

    const dash7Days = await request({ path: '/admin/dashboard?range=7days', cookie: adminCookie });
    assert(dash7Days.statusCode === 200, 'Dashboard lọc thành công 7 ngày qua (7days)');

    const dash30Days = await request({ path: '/admin/dashboard?range=30days', cookie: adminCookie });
    assert(dash30Days.statusCode === 200, 'Dashboard lọc thành công 30 ngày qua (30days)');

    const dashCustom = await request({ path: '/admin/dashboard?range=custom&from_date=2026-01-01&to_date=2026-12-31', cookie: adminCookie });
    assert(dashCustom.statusCode === 200, 'Dashboard lọc thành công theo khoảng ngày tùy chọn (from_date - to_date)');

    // -------------------------------------------------------------
    // BƯỚC 3: KIỂM THỬ CRUD VÀ BỘ LỌC LỚP HỌC YOGA
    // -------------------------------------------------------------
    console.log('\n--- 3. CRUD & BỘ LỌC LỚP HỌC YOGA (/admin/classes) ---');
    // Filter test
    const classFilter = await request({ path: '/admin/classes?q=Yoga&teacher_id=1', cookie: adminCookie });
    assert(classFilter.statusCode === 200 && classFilter.body.includes('Tìm theo tên lớp'), 'Thanh tìm kiếm & lọc lớp học hoạt động (Keyword + Teacher + Schedule)');

    // CREATE
    const createClassRes = await request({
        path: '/admin/classes/create',
        method: 'POST',
        cookie: adminCookie
    }, querystring.stringify({
        name: 'Yoga Test Automation Class',
        teacher_id: 1,
        lich_hoc: 'Thứ 2 - 4 - 6',
        start_time: '07:00',
        end_time: '08:15',
        start_date: '2026-11-10',
        end_date: '2026-12-31',
        quantity: 20,
        price: '550000',
        location: 'Phòng Sen Vàng - Test',
        description: 'Lớp tạo tự động bởi automated test suite'
    }));
    assert(createClassRes.statusCode === 302, 'Tạo lớp học Yoga mới thành công (HTTP 302)');

    const newClassDb = queryDb("SELECT id, name FROM classes WHERE name LIKE '%Automation%' ORDER BY id DESC LIMIT 1;");
    const classIdMatch = newClassDb ? newClassDb.split('\n')[1] : null;
    const newClassId = classIdMatch ? classIdMatch.split('\t')[0] : null;
    assert(newClassId !== null, `Xác nhận lớp học đã lưu vào Database với ID #${newClassId}`);

    // UPDATE
    if (newClassId) {
        const updateClassRes = await request({
            path: `/admin/classes/${newClassId}/edit`,
            method: 'POST',
            cookie: adminCookie
        }, querystring.stringify({
            name: 'Yoga Test Automation Class (Updated)',
            teacher_id: 1,
            lich_hoc: 'Thứ 3 - 5 - 7',
            start_time: '07:30',
            end_time: '08:45',
            start_date: '2026-11-10',
            end_date: '2026-12-31',
            quantity: 25,
            price: '600000',
            location: 'Phòng Trúc Xanh - Test',
            description: 'Lớp đã được cập nhật thông tin'
        }));
        assert(updateClassRes.statusCode === 302, 'Cập nhật thông tin lớp học thành công (HTTP 302)');

        const updatedClassDb = queryDb(`SELECT name, quantity FROM classes WHERE id = ${newClassId};`);
        assert(updatedClassDb && updatedClassDb.includes('Updated'), 'Xác nhận lớp học đã được cập nhật dữ liệu trong Database');

        // DELETE
        const deleteClassRes = await request({
            path: `/admin/classes/${newClassId}/delete`,
            method: 'POST',
            cookie: adminCookie
        });
        assert(deleteClassRes.statusCode === 302, 'Xóa lớp học thành công (HTTP 302)');

        const checkDeleted = queryDb(`SELECT count(*) FROM classes WHERE id = ${newClassId};`);
        assert(checkDeleted && checkDeleted.includes('0'), 'Xác nhận lớp học đã bị xóa hoàn toàn khỏi Database');
    }

    // -------------------------------------------------------------
    // BƯỚC 4: KIỂM THỬ CRUD VÀ BỘ LỌC GIÁO VIÊN (/admin/teachers)
    // -------------------------------------------------------------
    console.log('\n--- 4. CRUD & BỘ LỌC GIÁO VIÊN (/admin/teachers) ---');
    const teacherFilter = await request({ path: '/admin/teachers?exp_range=%3E5', cookie: adminCookie });
    assert(teacherFilter.statusCode === 200 && teacherFilter.body.includes('Tìm theo tên giáo viên'), 'Thanh tìm kiếm & lọc giáo viên hoạt động (Kinh nghiệm > 5 năm)');

    // CREATE
    const createTeacherRes = await request({
        path: '/admin/teachers/create',
        method: 'POST',
        cookie: adminCookie
    }, querystring.stringify({
        name: 'HLV Phan Minh Tri Test',
        phone: '0988776655',
        email: 'minhtri.test@vitayoga.vn',
        birthday: '1990-05-15',
        exp_year: 7,
        description: 'Chuyên gia giảng dạy Thiền định và Asana cơ bản',
        avatar: 'tu.jpg'
    }));
    assert(createTeacherRes.statusCode === 302, 'Tạo hồ sơ giáo viên mới thành công (HTTP 302)');

    const newTeacherDb = queryDb("SELECT id, name FROM teachers WHERE email = 'minhtri.test@vitayoga.vn' LIMIT 1;");
    const teacherIdMatch = newTeacherDb ? newTeacherDb.split('\n')[1] : null;
    const newTeacherId = teacherIdMatch ? teacherIdMatch.split('\t')[0] : null;
    assert(newTeacherId !== null, `Xác nhận hồ sơ giáo viên đã lưu vào Database với ID #${newTeacherId}`);

    // UPDATE
    if (newTeacherId) {
        const updateTeacherRes = await request({
            path: `/admin/teachers/${newTeacherId}/edit`,
            method: 'POST',
            cookie: adminCookie
        }, querystring.stringify({
            name: 'HLV Phan Minh Tri (Master Test)',
            phone: '0988776699',
            email: 'minhtri.test@vitayoga.vn',
            birthday: '1990-05-15',
            exp_year: 8,
            description: 'Master HLV Yoga với 8 năm kinh nghiệm',
            avatar: 'tu.jpg'
        }));
        assert(updateTeacherRes.statusCode === 302, 'Cập nhật hồ sơ giáo viên thành công (HTTP 302)');

        const updatedTeacherDb = queryDb(`SELECT name, exp_year FROM teachers WHERE id = ${newTeacherId};`);
        assert(updatedTeacherDb && updatedTeacherDb.includes('Master'), 'Xác nhận hồ sơ giáo viên đã được cập nhật trong Database');

        // DELETE
        const deleteTeacherRes = await request({
            path: `/admin/teachers/${newTeacherId}/delete`,
            method: 'POST',
            cookie: adminCookie
        });
        assert(deleteTeacherRes.statusCode === 302, 'Xóa hồ sơ giáo viên thành công (HTTP 302)');

        const checkTeacherDeleted = queryDb(`SELECT count(*) FROM teachers WHERE id = ${newTeacherId};`);
        assert(checkTeacherDeleted && checkTeacherDeleted.includes('0'), 'Xác nhận giáo viên đã bị xóa hoàn toàn khỏi Database');
    }

    // -------------------------------------------------------------
    // BƯỚC 5: KIỂM THỬ CRUD VÀ BỘ LỌC HỌC VIÊN (/admin/customers)
    // -------------------------------------------------------------
    console.log('\n--- 5. CRUD & BỘ LỌC HỌC VIÊN (/admin/customers) ---');
    const customerFilter = await request({ path: '/admin/customers?gender=female', cookie: adminCookie });
    assert(customerFilter.statusCode === 200 && customerFilter.body.includes('Tìm theo họ tên'), 'Thanh tìm kiếm & lọc học viên hoạt động (Giới tính Nữ)');

    // CREATE
    const createCustomerRes = await request({
        path: '/admin/customers/create',
        method: 'POST',
        cookie: adminCookie
    }, querystring.stringify({
        name: 'Do Hoang Yen Test',
        phone: '0977112233',
        email: 'hoangyen.test@gmail.com',
        birthday: '1998-10-20',
        gender: 'female',
        address: 'Cau Giay, Ha Noi',
        note: 'Học viên đăng ký qua tự động test'
    }));
    assert(createCustomerRes.statusCode === 302, 'Thêm học viên mới thành công (HTTP 302)');

    const newCustomerDb = queryDb("SELECT id, name FROM customers WHERE phone = '0977112233' LIMIT 1;");
    const custIdMatch = newCustomerDb ? newCustomerDb.split('\n')[1] : null;
    const newCustId = custIdMatch ? custIdMatch.split('\t')[0] : null;
    assert(newCustId !== null, `Xác nhận học viên đã lưu vào Database với ID #${newCustId}`);

    // UPDATE
    if (newCustId) {
        const updateCustRes = await request({
            path: `/admin/customers/${newCustId}/edit`,
            method: 'POST',
            cookie: adminCookie
        }, querystring.stringify({
            name: 'Do Hoang Yen (VIP Member)',
            phone: '0977112233',
            email: 'hoangyen.test@gmail.com',
            birthday: '1998-10-20',
            gender: 'female',
            address: 'Ba Dinh, Ha Noi',
            note: 'Đã nâng cấp học viên VIP'
        }));
        assert(updateCustRes.statusCode === 302, 'Cập nhật thông tin học viên thành công (HTTP 302)');

        const updatedCustDb = queryDb(`SELECT name, address FROM customers WHERE id = ${newCustId};`);
        assert(updatedCustDb && updatedCustDb.includes('VIP'), 'Xác nhận thông tin học viên đã được cập nhật trong Database');

        // DELETE
        const deleteCustRes = await request({
            path: `/admin/customers/${newCustId}/delete`,
            method: 'POST',
            cookie: adminCookie
        });
        assert(deleteCustRes.statusCode === 302, 'Xóa học viên thành công (HTTP 302)');

        const checkCustDeleted = queryDb(`SELECT count(*) FROM customers WHERE id = ${newCustId};`);
        assert(checkCustDeleted && checkCustDeleted.includes('0'), 'Xác nhận học viên đã bị xóa hoàn toàn khỏi Database');
    }

    // -------------------------------------------------------------
    // BƯỚC 6: HỌC VIÊN ĐẶT LỚP & QUY TRÌNH DUYỆT ĐƠN
    // -------------------------------------------------------------
    console.log('\n--- 6. HỌC VIÊN ĐẶT LỚP & DUYỆT ĐƠN (/register & /registered-classes) ---');
    const bookRes = await request({
        path: '/register',
        method: 'POST',
        cookie: studentCookie
    }, querystring.stringify({
        name: 'Nguyễn Văn An',
        phone: '0901234567',
        email: 'student1@vitayoga.vn',
        class_id: 1,
        package_months: 6,
        note: 'AutoTestBooking'
    }));
    assert(bookRes.statusCode === 302, 'Học viên gửi đơn đăng ký lớp học thành công (HTTP 302)');

    const newRegDb = queryDb("SELECT id, status, package_months, final_price FROM registrations WHERE note = 'AutoTestBooking' ORDER BY id DESC LIMIT 1;");
    const regLine = newRegDb ? newRegDb.split('\n')[1] : null;
    const newRegId = regLine ? regLine.split('\t')[0] : null;
    const regStatus = regLine ? regLine.split('\t')[1] : null;
    assert(newRegId !== null && regStatus === 'PENDING', `Đơn đăng ký #${newRegId} đã tạo trong hệ thống với trạng thái PENDING`);

    // Admin duyệt đơn
    if (newRegId) {
        const approveRes = await request({
            path: `/admin/registrations/${newRegId}/approve`,
            method: 'POST',
            cookie: adminCookie
        });
        assert(approveRes.statusCode === 302, 'Admin phê duyệt đơn đăng ký thành công (HTTP 302)');

        const approvedDb = queryDb(`SELECT status FROM registrations WHERE id = ${newRegId};`);
        assert(approvedDb && approvedDb.includes('CONFIRMED'), `Trạng thái đơn #${newRegId} đã chuyển sang CONFIRMED`);

        // Học viên vào xem danh sách khóa học của mình
        const myClassesRes = await request({
            path: '/registered-classes',
            cookie: studentCookie
        });
        assert(myClassesRes.statusCode === 200 && /ĐÃ DUYỆT/i.test(myClassesRes.body), 'Học viên xem màn hình Khóa học của tôi thấy lớp đã được phê duyệt');

        // Cleanup test registration
        queryDb(`DELETE FROM attendances WHERE registration_id = ${newRegId};`);
        queryDb(`DELETE FROM registrations WHERE id = ${newRegId};`);
    }

    // -------------------------------------------------------------
    // BƯỚC 7: HỌC VIÊN CẬP NHẬT THÔNG TIN CÁ NHÂN & ĐỔI MẬT KHẨU
    // -------------------------------------------------------------
    console.log('\n--- 7. HỌC VIÊN CẬP NHẬT THÔNG TIN & ĐỔI MẬT KHẨU (/account) ---');
    // Cập nhật profile
    const updateProfileRes = await request({
        path: '/account/profile',
        method: 'POST',
        cookie: studentCookie
    }, querystring.stringify({
        name: 'Nguyễn Văn An Cập Nhật',
        phone: '0901234999',
        gender: 'male',
        address: 'Số 88 Phố Huế, Hai Bà Trưng, Hà Nội'
    }));
    assert(updateProfileRes.statusCode === 302, 'Học viên cập nhật thông tin cá nhân thành công (HTTP 302)');

    const updatedUserDb = queryDb("SELECT name FROM users WHERE user_name = 'student1';");
    assert(updatedUserDb && updatedUserDb.includes('Cập Nhật'), 'Tên học viên trong bảng users đã được cập nhật thành công');

    // Đổi mật khẩu thành '654321'
    const changePwdRes = await request({
        path: '/account/password',
        method: 'POST',
        cookie: studentCookie
    }, querystring.stringify({
        password: '654321',
        confirm_password: '654321'
    }));
    assert(changePwdRes.statusCode === 302, 'Học viên đổi mật khẩu thành công (HTTP 302)');

    // Thử đăng nhập lại bằng mật khẩu mới '654321'
    const newLoginRes = await request({
        path: '/account/login',
        method: 'POST'
    }, querystring.stringify({ username: 'student1', password: '654321' }));
    assert(newLoginRes.statusCode === 302 && newLoginRes.cookie, 'Học viên đăng nhập lại thành công bằng mật khẩu mới (654321)');

    // Hoàn trả mật khẩu về '123456' và tên gốc để bảo toàn dữ liệu mẫu
    if (newLoginRes.cookie) {
        await request({
            path: '/account/password',
            method: 'POST',
            cookie: newLoginRes.cookie
        }, querystring.stringify({
            password: '123456',
            confirm_password: '123456'
        }));
        await request({
            path: '/account/profile',
            method: 'POST',
            cookie: newLoginRes.cookie
        }, querystring.stringify({
            name: 'Nguyễn Văn An',
            phone: '0901234567',
            gender: 'male',
            address: 'Hà Nội'
        }));
    }

    // -------------------------------------------------------------
    // TỔNG KẾT KẾT QUẢ KIỂM THỬ
    // -------------------------------------------------------------
    console.log('\n======================================================================');
    console.log(`📊 TỔNG KẾT KẾT QUẢ TEST: ${passed} PASSED / ${passed + failed} TEST CASES`);
    if (failed === 0) {
        console.log('🎉 TẤT CẢ CÁC TÍNH NĂNG VÀ NGHIỆP VỤ HOẠT ĐỘNG HOÀN HẢO 100%!');
    } else {
        console.log(`⚠️ Có ${failed} ca kiểm thử không thành công, vui lòng kiểm tra.`);
    }
    console.log('======================================================================\n');
}

// Chờ 3 giây cho server hoàn tất khởi động rồi chạy
setTimeout(() => {
    runTests().catch(console.error);
}, 3000);
