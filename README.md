# Into The Deep Code

Code for the robot made by [PrimeTech (RO025)](https://primetechrobotics.com/) for the [FTC competition](https://www.firstinspires.org/robotics/ftc) in the [2024-2025 season](https://info.firstinspires.org/free-season-content?_gl=1*1kildcj*_gcl_au*MTQxNDczMjA3My4xNzI1NzI2MDQ0*_ga*MTg2MDkzNDIxMC4xNzI1NzI2MDM1*_ga_YCHVEBFMCS*MTcyNjc0NDk3NC4yLjAuMTcyNjc0NDk3NC4wLjAuMA..).

## For people using this as inspiration:
### Structure (in `TeamCode`)
- `PrimeTech`: code based on [FSMs](https://gm0.org/en/latest/docs/software/concepts/finite-state-machines.html), structured with components, used only for TeleOp. Also includes an Auto OpMode for a pushbot (only drivetrain movement). **⚠ IT SHOULD NOT BE USED AS INSPIRATION, AS IT'S CHAOTIC AND UNSTRUCTURED!**
- `PrimeTechV2`: code using the [NextFTC](https://nextftc.dev/) *command-based* library. It's well structured, but works only with NextFTC, and as of the time of writing this, we use our own library. **If possible, don't use it as inspiration.**
- ✔ `PrimeTechV3`: use this for inspiration, as it has the best structure, and code. During the season, it was used only for Auto, but it also works for TeleOp. Also, this is written in Kotlin, so it's automatically good 🐐

## Made using [Pedro Pathing Quickstart](https://github.com/Pedro-Pathing/Quickstart)

[Pedro Pathing docs](https://pedropathing.com/).

### 👨‍💻 Authors: [Robert Lupas](https://github.com/RobertLupas/), [Matei Chiorean](https://github.com/matei135), [Tudor Ceclan](https://github.com/Tud8r)
