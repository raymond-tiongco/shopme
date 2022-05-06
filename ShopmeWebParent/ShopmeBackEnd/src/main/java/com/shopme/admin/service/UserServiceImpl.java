package com.shopme.admin.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import com.shopme.admin.utils.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo,
                           RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveRootUser(User rootUser) {
        rootUser.setPassword(passwordEncoder.encode(rootUser.getPassword()));
        userRepo.save(rootUser);
    }

    @Override
    public User saveUser(User user, ArrayList<Integer> enabledList, ArrayList<Integer> roles,
                         MultipartFile photo) throws IOException {

        Optional<User> optionalUser = Optional.ofNullable(user);

        if (optionalUser.isPresent() ) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Optional<ArrayList<Integer>> optionalRoles = Optional.ofNullable(roles);
            if (optionalRoles.isPresent()) {
                List<Role> roleList = optionalRoles.get().stream().filter(role -> role > 0)
                        .map(id -> roleService.findOne(id)).collect(Collectors.toList());
                user.getRoles().addAll(roleList);
            } else {
                throw new NullPointerException("Parameter \"roles\" of type ArrayList<Integer> is null");
            }

            Optional<MultipartFile> optionalMultipartFile = Optional.ofNullable(photo);
            if (optionalMultipartFile.isPresent()) {
                user.setPhotos(photo.getBytes());
            } else {
                throw new NullPointerException("Parameter \"photo\" of type MultipartFile is null");
            }

            Optional<ArrayList<Integer>> enabledOptional = Optional.ofNullable(enabledList);
            if (enabledOptional.isPresent()) {
                Optional<Integer> enabled = enabledOptional.get().stream().filter(enable -> enable > 0).findFirst();
                user.setEnabled(enabled.orElse(0));
            } else {
                throw new NullPointerException("Parameter \"enabledLIst\" of type ArrayList<Integer> is null");
            }

            return userRepo.save(user);
        } else {
            throw new NullPointerException("Parameter \"user\" of type User is null");
        }
    }

    @Override
    public void saveRole(String name, String description) {
        roleRepo.save(new Role(name, description));
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        User user = userRepo.findByEmail(email);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public void deleteById(int userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public User findById(int userId) {
    	return userRepo.findById(userId).orElseThrow((() -> new RuntimeException("did not find user id - "+userId)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username);
        Objects.requireNonNull(user, "User not found in the database");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }

	@Override
	public String getBase64(User user) {
		try {
			byte[] encodeBase64 = Base64.getEncoder().encode(user.getPhotos());
			return new String(encodeBase64, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "UnsupportedEncodingException when converting file to base64";
		}
	}

	@Override
	public byte[] getBytes(User user) {
		return user.getPhotos();
	}

	@Override
	public void getImageAsStream(int id, HttpServletResponse response) {
		response.setContentType("image/png");

    	try (InputStream inputStream = new ByteArrayInputStream(getBytes(findById(id)))) {
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception ignored) {}
	}

    @Override
    public void enable(int userid) {
        Optional<User> optionalUser = Optional.ofNullable(
                userRepo.findById(userid)
                        .orElseThrow(() -> new RuntimeException("Cannot find user with id of "+userid)));

        optionalUser.ifPresent(user -> user.setEnabled(1));
    }

    @Override
    public void disable(int userid) {
        Optional<User> optionalUser = Optional.ofNullable(
                userRepo.findById(userid)
                        .orElseThrow(() -> new RuntimeException("Cannot find user with id of "+userid)));

        optionalUser.ifPresent(user -> user.setEnabled(0));
    }

    @Override
    public List<User> findByEmailLike(String email) {
        return userRepo.searchByEmailLike(email);
    }

    @Override
    public Page<User> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 5);

        return userRepo.findAll(pageable);
    }

    @Override
    public Page<User> findUserWithSort(String field, String direction, int pageNumber) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(field).ascending() : Sort.by(field).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);

        return userRepo.findAll(pageable);
    }

    @Override
    public void exportToCsv(Writer writer) {
        List<User> users = findAll();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("ID", "E-mail", "Firstname", "Lastname", "Enabled", "Roles");

            for (User user : users) {
                csvPrinter.printRecord(user.getId(), user.getEmail(), user.getFirstName(),
                        user.getLastName(), user.getEnabled() == 1 ? "Yes" : "No", user.getRoles());
            }
        } catch (IOException e) {
            Log.error("Error While writing CSV: "+e);
        }
    }

    @Override
    public ByteArrayInputStream exportToExcel(List<User> users) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("users");

            Row row = sheet.createRow(0);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell cell = row.createCell(0);
            cell.setCellValue("ID");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("E-mail");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Firstname");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Lastname");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Enabled");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Roles");
            cell.setCellStyle(headerCellStyle);

            for (int i = 0; i < users.size(); i++) {
                Row dataRow = sheet.createRow(i+1);

                dataRow.createCell(0).setCellValue(users.get(i).getId());
                dataRow.createCell(1).setCellValue(users.get(i).getEmail());
                dataRow.createCell(2).setCellValue(users.get(i).getFirstName());
                dataRow.createCell(3).setCellValue(users.get(i).getLastName());
                dataRow.createCell(4).setCellValue(users.get(i).getEnabled() == 1 ? "Yes" : "No");
                dataRow.createCell(5).setCellValue(users.get(i).getRoles().toString());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            Log.error("Error While writing Excel: "+e);
            return null;
        }
        // https://simplesolution.dev/spring-boot-download-excel-file-export-from-mysql/
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(java.awt.Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(java.awt.Color.WHITE);

        cell.setPhrase(new Phrase("User ID", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Full Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Roles", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<User> listUsers) {
        for (User user : listUsers) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName()+" "+user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(user.getEnabled() == 1 ? "Yes" : "No");
        }
    }

    @Override
    public void exportToPdf(HttpServletResponse response) {
        try {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(java.awt.Color.BLUE);

            Paragraph p = new Paragraph("List of Users", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100f);
            table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
            table.setSpacingBefore(10);

            writeTableHeader(table);
            writeTableData(table, findAll());

            document.add(table);
            document.close();

        } catch (Exception e) {
            Log.error("Error While writing PDF: "+e);
        }
        //  https://www.codejava.net/frameworks/spring-boot/pdf-export-example
    }
}